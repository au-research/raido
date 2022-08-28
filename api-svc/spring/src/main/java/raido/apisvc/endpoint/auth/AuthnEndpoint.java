package raido.apisvc.endpoint.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import raido.apisvc.service.auth.GoogleOidc;
import raido.apisvc.service.auth.RaidV2AuthService;
import raido.apisvc.service.auth.RaidV2AuthService.AuthzTokenPayload;
import raido.apisvc.spring.config.environment.AafOidcProps;
import raido.apisvc.spring.config.environment.RaidoAuthnProps;
import raido.apisvc.spring.security.ApiSafeException;
import raido.apisvc.util.Log;
import raido.apisvc.util.RestUtil;

import java.io.IOException;

import static raido.apisvc.spring.security.IdProviderException.idpException;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.isNullOrEmpty;
import static raido.apisvc.util.StringUtil.trimEqualsIgnoreCase;


@RequestMapping
@RestController
public class AuthnEndpoint {
  public static final String IDP_URL = "/idpresponse";

  private static final Log log = to(AuthnEndpoint.class);

  private RaidoAuthnProps raido;
  private AafOidcProps aaf;
  private GoogleOidc google;
  
  private RestTemplate rest;
  private ObjectMapper map;
  
  private RaidV2AuthService raidv2Auth;

  public AuthnEndpoint(
    RaidoAuthnProps raido,
    AafOidcProps aaf,
    GoogleOidc google,
    RestTemplate rest, ObjectMapper map,
    RaidV2AuthService raidv2Auth
  ) {
    this.raido = raido;
    this.aaf = aaf;
    this.google = google;
    this.rest = rest;
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


    if( trimEqualsIgnoreCase(state.clientId, aaf.clientId) ){
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

      MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
      map.add("code", idpResponseCode);
      map.add("client_id", aaf.clientId);
      map.add("client_secret", aaf.clientSecret);
      map.add("grant_type", "authorization_code");
      map.add("redirect_uri", raido.serverRedirectUri);

      HttpEntity<MultiValueMap<String, String>> request =
        new HttpEntity<>(map, headers);

//      log.with("bod", request.getBody()).info();

      ResponseEntity<String> response = rest.postForEntity(
        aaf.tokenUrl, request, String.class);

      log.with("response", response).
        with("response.body", response.getBody()).
        info("aaf response");

      res.sendRedirect( "%s#id_token=%s".formatted(
        state.redirectUri, "xxx.yyy.zzz"
      ));
      
    }
    else if( google.canHandle(state.clientId) ){
      DecodedJWT jwt = google.exchangeCodeForJwt(idpResponseCode);
      log.info("google succeeded");
      res.sendRedirect( "%s#id_token=%s".formatted(
        state.redirectUri, 
        raidv2Auth.sign( new AuthzTokenPayload().
          setSubject(jwt.getSubject()).
          setClientId(state.clientId).
          setEmail(jwt.getClaim("email").asString()).
          setRole("none")
        )
      ));
    }

    // improve should be a specific authn error?
    // and should it expose the error?
    throw idpException("unknown clientId %s", state.clientId);
  }

}

