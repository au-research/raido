package raido.apisvc.endpoint.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.repository.AppUserRepository;
import raido.apisvc.service.auth.RaidV2AppUserAuthService;
import raido.apisvc.spring.config.environment.RaidoAuthnProps;
import raido.apisvc.spring.security.ApiSafeException;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;
import raido.apisvc.util.Nullable;
import raido.apisvc.util.RestUtil;
import raido.db.jooq.api_svc.enums.IdProvider;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.eclipse.jetty.util.TypeUtil.isFalse;
import static raido.apisvc.spring.security.raidv2.AuthzTokenPayload.AuthzTokenPayloadBuilder.anAuthzTokenPayload;
import static raido.apisvc.service.auth.NonAuthzTokenPayload.NonAuthzTokenPayloadBuilder.aNonAuthzTokenPayload;
import static raido.apisvc.spring.security.IdProviderException.idpException;
import static raido.apisvc.util.ExceptionUtil.authFailed;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.blankToDefault;
import static raido.apisvc.util.StringUtil.isBlank;
import static raido.apisvc.util.StringUtil.isNullOrEmpty;
import static raido.db.jooq.api_svc.enums.IdProvider.ORCID;


@RequestMapping
@RestController
public class AppUserAuthnEndpoint {
  public static final String IDP_URL = "/idpresponse";

  private static final Log log = to(AppUserAuthnEndpoint.class);
  public static final String EMAIL_CLAIM = "email";

  private ObjectMapper map;
  
  private RaidV2AppUserAuthService raidV2UserAuthSvc;
  private RaidoAuthnProps authnProps;
  private AppUserRepository appUserRepo;

  public AppUserAuthnEndpoint(
    ObjectMapper map,
    RaidV2AppUserAuthService raidV2UserAuthSvc,
    RaidoAuthnProps authnProps,
    AppUserRepository appUserRepo
  ) {
    this.map = map;
    this.raidV2UserAuthSvc = raidV2UserAuthSvc;
    this.authnProps = authnProps;
    this.appUserRepo = appUserRepo;
  }

  record AuthState(String redirectUri, String clientId) { }
  
  @GetMapping(IDP_URL)
  public void authenticate(
    HttpServletRequest req, HttpServletResponse res
  ) throws IOException, ApiSafeException {
    // log only the keySet, not the values, don't want authn codes in logs
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

    if( !isAllowedRedirectUri(
      authnProps.getAllowedClientRedirectUris(), state.redirectUri )
    ){
      log.with("redirectUri", state.redirectUri).
        error("redirectUri not in allowed list");
      throw authFailed();
    }

    DecodedJWT idProviderJwt = raidV2UserAuthSvc.
      exchangeCodeForVerifiedJwt(state.clientId, idpResponseCode);

    /* "email" isn't really email any more, going to rename it to 
    "description" or something. 
    Orcid may not have an email, user doesn't have to make it public.
    Google always provides email.
    AAF, not sure if it's always there - can only say it's been there for 
    everyone from ARDC so far. */
    String email = idProviderJwt.getClaim(EMAIL_CLAIM).asString();
    String subject = idProviderJwt.getSubject();

    /* Really not sure this is a good idea.
    It means that each time orcid users change their permissions (potentially
    three times between email, name, neither), the user will be counted as
    a separate user, because we use a tuple of [email, subject, clientId] to 
    link the "id_token" to an app_user record.  That means they'll have to be
    authorized by someone again, too.
    I think we're going to have to just use subject.
    This new approach would align with our usage of app_user table for api-keys
    too. */
    if( raidV2UserAuthSvc.mapIdProvider(state.clientId) == ORCID ){
      // try name fields, but that's also allowed to be private
      email = formatOrcidName(idProviderJwt).
        // otherwise, fallback to the subject
        // note the email field forces to lowercase, so no good using branding  
        orElseGet(()-> "orcid " + subject);
    }

    Guard.hasValue(email);
    Guard.hasValue(subject);

    var userRecord = appUserRepo.
      getAppUserRecord(email, subject, state.clientId); 
    if( userRecord.isEmpty() ){
      log.with("email", email).
        with("subject", subject).
        with("clientId", state.clientId).
        debug("identified new NonAuthz user");
      // valid: authenticated via an IdP but not authorized/approved as a user
      res.sendRedirect( "%s#id_token=%s".formatted(
        state.redirectUri,
        raidV2UserAuthSvc.sign( aNonAuthzTokenPayload().
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
        info("api-key users cannot authenticate using user service");
      throw authFailed();
    }

    res.sendRedirect("%s#id_token=%s".formatted(
      state.redirectUri,
      raidV2UserAuthSvc.sign(anAuthzTokenPayload().
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

  private static Optional<String> formatOrcidName(DecodedJWT idProviderJwt) {
    String givenName = idProviderJwt.getClaim("given_name").asString();
    String familyName = idProviderJwt.getClaim("family_name").asString();

    String formattedName = blankToDefault(givenName, "") +
      " " + blankToDefault(familyName, "");
    formattedName = formattedName.trim();
    if( isBlank(formattedName) ){
      return Optional.empty();
    }
    
    return Optional.of(formattedName);
  }

  public static boolean isAllowedRedirectUri(
    List<String> allowedUris, 
    @Nullable String redirectUri
  ){
    if( redirectUri == null ){
      return false;
    }
    String normalisedUri = redirectUri.trim().toLowerCase();
    return allowedUris.stream().anyMatch(normalisedUri::startsWith);
  }

}

