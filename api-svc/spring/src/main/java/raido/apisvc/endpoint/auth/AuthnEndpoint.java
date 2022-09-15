package raido.apisvc.endpoint.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.service.auth.AafOidc;
import raido.apisvc.service.auth.AuthzTokenPayload;
import raido.apisvc.service.auth.GoogleOidc;
import raido.apisvc.service.auth.NonAuthzTokenPayload;
import raido.apisvc.service.auth.RaidV2AuthService;
import raido.apisvc.service.auth.RaidoClaim;
import raido.apisvc.spring.security.ApiSafeException;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;
import raido.apisvc.util.RestUtil;
import raido.db.jooq.api_svc.enums.IdProvider;

import java.io.IOException;

import static raido.apisvc.service.auth.AuthzTokenPayload.AuthzTokenPayloadBuilder.anAuthzTokenPayload;
import static raido.apisvc.service.auth.NonAuthzTokenPayload.NonAuthzTokenPayloadBuilder.aNonAuthzTokenPayload;
import static raido.apisvc.spring.security.IdProviderException.idpException;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.isTrue;
import static raido.apisvc.util.StringUtil.isNullOrEmpty;


@RequestMapping
@RestController
public class AuthnEndpoint {
  public static final String IDP_URL = "/idpresponse";

  private static final Log log = to(AuthnEndpoint.class);

  private AafOidc aaf;
  private GoogleOidc google;

  private ObjectMapper map;
  
  private RaidV2AuthService raidv2Auth;

  public AuthnEndpoint(
    AafOidc aaf,
    GoogleOidc google,
    ObjectMapper map,
    RaidV2AuthService raidv2Auth
  ) {
    this.aaf = aaf;
    this.google = google;
    this.map = map;
    this.raidv2Auth = raidv2Auth;
  }

  record AuthState(String redirectUri, String clientId) { }
  
  @GetMapping(IDP_URL)
  public void authenticate(
    HttpServletRequest req, HttpServletResponse res
  ) throws IOException, ApiSafeException {
    // don't log values, don't want authn codes in logs
    log.with("params", req.getParameterMap().keySet()).debug("/idpresponse");
    
    String idpResponseCode = req.getParameter("code");
    if( isNullOrEmpty(idpResponseCode) ){
      throw idpException("no code provided in query params");
    }

    String stateValue = req.getParameter("state");
    if( isNullOrEmpty(stateValue) ){
      throw idpException("no state provided in query params");
    }

    String decodedState = RestUtil.base64Decode(stateValue);
    log.with("decodeState", decodedState).debug("");
    
    var state = map.readValue(decodedState, AuthState.class);
    
    if( isNullOrEmpty(state.clientId) ){
      throw idpException("no clientId provided in state"); 
    }

    // security:sto validate the redirect uri 


    var idProvider = mapIdProvider(state.clientId);
    DecodedJWT idProviderJwt;
    switch( idProvider ){
      case GOOGLE -> idProviderJwt = google.exchangeCodeForJwt(idpResponseCode);
      case AAF -> idProviderJwt = aaf.exchangeCodeForJwt(idpResponseCode);
      default -> throw idpException("unknown clientId %s", state.clientId); 
    }

    String email = idProviderJwt.getClaim("email").asString().
      toLowerCase().trim();
    String subject = idProviderJwt.getSubject();
    Guard.hasValue(email);
    Guard.hasValue(subject);

    var userRecord = raidv2Auth.getAppUserRecord(email, subject, state.clientId); 
    if( userRecord.isEmpty() ){
      // valid: authenticated via an IdP but not authorized/approved as a user
      res.sendRedirect( "%s#id_token=%s".formatted(
        state.redirectUri,
        raidv2Auth.sign( aNonAuthzTokenPayload().
          withSubject(idProviderJwt.getSubject()).
          withClientId(state.clientId).
          withEmail(idProviderJwt.getClaim("email").asString()).
          build()
        )
      ));
      return;
    }

    var user = userRecord.get();
    if( isTrue(user.getDisabled()) ){
      // SP would need to look in their user list to know user is disabled
      log.with("appUserId", user.getId()).
        with("email", user.getEmail()).
        info("user tried to authenticate, but is disabled");
      res.sendRedirect( "%s#id_token=%s".formatted(
        state.redirectUri,
        raidv2Auth.sign( aNonAuthzTokenPayload().
          withSubject(idProviderJwt.getSubject()).
          withClientId(state.clientId).
          withEmail(idProviderJwt.getClaim("email").asString()).
          build()
        )
      ));
    }

    res.sendRedirect("%s#id_token=%s".formatted(
      state.redirectUri,
      raidv2Auth.sign(anAuthzTokenPayload().
        withAppUserId(user.getId()).
        withSubject(idProviderJwt.getSubject()).
        withClientId(state.clientId).
        withEmail(idProviderJwt.getClaim(RaidoClaim.EMAIL.getId()).asString()).
        withRole(user.getRole().getLiteral()).
        build()
      )
    ));
  }


  public IdProvider mapIdProvider(String clientId){
    if( aaf.canHandle(clientId) ){
      return IdProvider.AAF;
    }
    else if( google.canHandle(clientId) ){
      return IdProvider.GOOGLE;
    }
    else {
      // improve should be a specific authn error?
      // and should it expose the error?
      throw idpException("unknown clientId %s", clientId);
    }
  }

}

