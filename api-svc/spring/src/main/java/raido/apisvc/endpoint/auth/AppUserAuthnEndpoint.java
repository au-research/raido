package raido.apisvc.endpoint.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.service.auth.RaidV2AppUserAuthService;
import raido.apisvc.service.auth.RaidoClaim;
import raido.apisvc.spring.security.ApiSafeException;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;
import raido.apisvc.util.RestUtil;
import raido.apisvc.util.StringUtil;
import raido.db.jooq.api_svc.enums.IdProvider;

import java.io.IOException;

import static org.eclipse.jetty.util.TypeUtil.isFalse;
import static raido.apisvc.service.auth.AuthzTokenPayload.AuthzTokenPayloadBuilder.anAuthzTokenPayload;
import static raido.apisvc.service.auth.NonAuthzTokenPayload.NonAuthzTokenPayloadBuilder.aNonAuthzTokenPayload;
import static raido.apisvc.spring.security.IdProviderException.idpException;
import static raido.apisvc.util.ExceptionUtil.authFailed;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.hasValue;
import static raido.apisvc.util.StringUtil.isNullOrEmpty;
import static raido.db.jooq.api_svc.enums.IdProvider.ORCID;


@RequestMapping
@RestController
public class AppUserAuthnEndpoint {
  public static final String IDP_URL = "/idpresponse";

  private static final Log log = to(AppUserAuthnEndpoint.class);
  public static final String EMAIL_CLAIM = "email";

  private ObjectMapper map;
  
  private RaidV2AppUserAuthService raidv2UserAuthSvc;

  public AppUserAuthnEndpoint(
    ObjectMapper map,
    RaidV2AppUserAuthService raidv2UserAuthSvc
  ) {
    this.map = map;
    this.raidv2UserAuthSvc = raidv2UserAuthSvc;
  }

  record AuthState(String redirectUri, String clientId) { }
  
  @GetMapping(IDP_URL)
  public void authenticate(
    HttpServletRequest req, HttpServletResponse res
  ) throws IOException, ApiSafeException {
    // don't log values, don't want authn codes in logs
    log.with("params", req.getParameterMap().keySet()).debug("/idpresponse");
    // security:sto remove this after orcid is working 
    log.with("params", req.getParameterMap()).debug("/idpresponse");
    
    String idpResponseCode = req.getParameter("code");
    if( isNullOrEmpty(idpResponseCode) ){
      throw idpException("no code provided in query params");
    }

    String stateValue = req.getParameter("state");
    if( isNullOrEmpty(stateValue) ){
      throw idpException("no state provided in query params");
    }

    String decodedState = RestUtil.base64Decode(stateValue);
    log.with("decodeState", decodedState).info("");
    
    var state = map.readValue(decodedState, AuthState.class);
    log.with("state", state).info("");
    
    if( isNullOrEmpty(state.clientId) ){
      throw idpException("no clientId provided in state"); 
    }

    // security:sto validate the redirect uri 


    DecodedJWT idProviderJwt = raidv2UserAuthSvc.
      exchangeCodeForVerfiedJwt(state.clientId, idpResponseCode);

    /* "email" isn't really email any more, going to rename it to 
    "description" or something. 
    Orcid may not have an email, user doesn't have to make it public.
    Google always provides email.
    AAF, not sure if it's always there - can only say it's been there for 
    everyone from ARDC so far. */
    String email = idProviderJwt.getClaim(EMAIL_CLAIM).asString();
    String subject = idProviderJwt.getSubject();

    /* Really not sure this is a good idea.
    It means that each time an orcid users frobs their permissions (potentially
    three times between email, name, neither), the user will be counted as
    a separate user, because we use a tuple of [email, subject, clientId] to 
    link the "id_token" to an app_user record.
    I think we're going to have to just use subject.
    This new approach would align with our usage of app_user table for api-keys
    too. */
    if( raidv2UserAuthSvc.mapIdProvider(state.clientId) == ORCID  ){
      // try name fields, but that's also allowed to be private
      email = idProviderJwt.getClaim("given_name").asString() + " " +
        idProviderJwt.getClaim("family_name").asString();
      
      // fall back to just using the orcid id stored in the JWT subject
      if( isNullOrEmpty(email) ){
        email = "ORCiD " + subject;
      }
    }

    Guard.hasValue(email);
    Guard.hasValue(subject);

    var userRecord = raidv2UserAuthSvc.
      getAppUserRecord(email, subject, state.clientId); 
    if( userRecord.isEmpty() ){
      // valid: authenticated via an IdP but not authorized/approved as a user
      res.sendRedirect( "%s#id_token=%s".formatted(
        state.redirectUri,
        raidv2UserAuthSvc.sign( aNonAuthzTokenPayload().
          withSubject(idProviderJwt.getSubject()).
          withClientId(state.clientId).
          withEmail(email).
          build()
        )
      ));
      return;
    }

    var user = userRecord.get();
    if( isFalse(user.getEnabled()) ){
      // SP would need to look in their user list to know user is disabled
      log.with("appUserId", user.getId()).
        with("email", user.getEmail()).
        info("user tried to authenticate, but is disabled");
      throw authFailed();
    }
    
    if( user.getIdProvider() == IdProvider.RAIDO_API ){
      log.with("appUserId", user.getId()).
        with("subject", user.getSubject()).
        info("api-key users cannot authneticate using user service");
      throw authFailed();
    }

    res.sendRedirect("%s#id_token=%s".formatted(
      state.redirectUri,
      raidv2UserAuthSvc.sign(anAuthzTokenPayload().
        withAppUserId(user.getId()).
        withServicePointId(user.getServicePointId()).
        withSubject(idProviderJwt.getSubject()).
        withClientId(state.clientId).
        withEmail(email).
        withRole(user.getRole().getLiteral()).
        build()
      )
    ));
  }


}

