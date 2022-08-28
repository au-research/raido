package raido.apisvc.service.auth;

import com.auth0.jwk.InvalidPublicKeyException;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import raido.apisvc.spring.config.environment.GoogleOidcProps;
import raido.apisvc.spring.config.environment.RaidoAuthnProps;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.StringJoiner;

import static raido.apisvc.spring.security.IdProviderException.idpException;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.mask;
import static raido.apisvc.util.StringUtil.trimEqualsIgnoreCase;

@Component
public class GoogleOidc {
  private static final Log log = to(GoogleOidc.class);
  
  private GoogleOidcProps google;
  private RaidoAuthnProps raido;

  private RestTemplate rest;

  public GoogleOidc(
    GoogleOidcProps google,
    RaidoAuthnProps raido,
    RestTemplate rest
  ) {
    this.google = google;
    this.raido = raido;
    this.rest = rest;
  }

  public boolean canHandle(String clientId){
    return trimEqualsIgnoreCase(clientId, google.clientId);    
  }
  
  public DecodedJWT exchangeCodeForJwt(String idpResponseCode){
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    var tokenRequest = new OAuthTokenRequest().
      code(idpResponseCode).
      client_id(google.clientId).
      client_secret(google.clientSecret).
      grant_type("authorization_code").
      redirect_uri(raido.serverRedirectUri);

    HttpEntity<OAuthTokenRequest> request =
      new HttpEntity<>(tokenRequest, headers);

//    log.with("bod", request.getBody()).info();

    ResponseEntity<OAuthTokenResponse> response = rest.postForEntity(
      google.tokenUrl, request, OAuthTokenResponse.class);


    log.with("response", response).
      with("response.body", response.getBody()).
      debug("google response");
    Guard.notNull(response.getBody());

    DecodedJWT jwt = JWT.decode(response.getBody().id_token);
    verify(jwt);
    
    return jwt;
  }
  
  public void verify(DecodedJWT jwt){
    Guard.areEqual(jwt.getAlgorithm(), "RS256");
    Guard.areEqual(jwt.getType(), "JWT");

    /* Probably overkill and unnecessary. If the attacker can intercept calls
    between api-svc and google to feed us a fake JWT, then they can intercept 
    the JWKS url call too.
    Keep an eye out for this when load testing; if measurably visible, 
    consider getting rid of this. */
    verifyGoogleJwksSignature(jwt);

    Guard.hasValue(jwt.getSubject());
    Guard.hasValue("id_token must have audience", jwt.getAudience());
    Guard.areEqual(google.clientId, jwt.getAudience().get(0));
    Guard.areEqual(google.issuer, jwt.getIssuer());
    
    Guard.isTrue("id_token must not be expired", 
      jwt.getExpiresAtAsInstant().
        // add a bit of leeway
        plusMillis(1000).
        isAfter(Instant.now()) );
    
    Guard.hasValue("email claim must have value", 
      jwt.getClaim("email").asString());
    
    Guard.isTrue("email_verified must be true", 
      jwt.getClaim("email_verified").asBoolean());
    
  }

  private void verifyGoogleJwksSignature(DecodedJWT jwt) {
    JwkProvider provider = null;
    try {
      /* Must use URL because google certs aren't at the well-known location.
      Note sure if can/should make provider static.  */
      provider = new UrlJwkProvider( new URL(google.jwks) );
    }
    catch( MalformedURLException e ){
      throw idpException("google props.jwks is malformed %s - %s",
        google.jwks, e.getMessage() );
    }

    Jwk jwk = null;
    try {
      jwk = provider.get(jwt.getKeyId());
    }
    catch( JwkException e ){
      // do not log signature
      log.with("header", jwt.getHeader()).
        with("payload", jwt.getPayload()).
        debug("could not get jwks key");
      throw idpException("could not get key id %s from %s - %s", 
        jwt.getKeyId(), google.jwks, e.getMessage() );
    }

    Algorithm algorithm = null;
    try {
      algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
    }
    catch( InvalidPublicKeyException e ){
      log.with("header", jwt.getHeader()).
        with("payload", jwt.getPayload()).
        debug("could not get jwks key");
      throw idpException("could not create RSA256 public key - %s",
        e.getMessage() );
    }

    algorithm.verify(jwt);
  }

  public static class OAuthTokenResponse {
    public String access_token;
    public int expires_in;
    public String scope;
    public String token_type;
    public String id_token;

    @Override
    public String toString() {
      return new StringJoiner(
        ", ",
        OAuthTokenResponse.class.getSimpleName() + "[",
        "]")
        .add("access_token='" + mask(access_token) + "'")
        .add("expires_in=" + expires_in)
        .add("scope='" + scope + "'")
        .add("token_type='" + token_type + "'")
        .add("id_token='" + mask(id_token) + "'")
        .toString();
    }
  }
  
  public static class OAuthTokenRequest {
    public String code;
    public String client_id;
    public String client_secret;
    public String grant_type;
    public String redirect_uri;

    public OAuthTokenRequest code(String code) {
      this.code = code;
      return this;
    }

    public OAuthTokenRequest client_id(String client_id) {
      this.client_id = client_id;
      return this;
    }

    public OAuthTokenRequest client_secret(String client_secret) {
      this.client_secret = client_secret;
      return this;
    }

    public OAuthTokenRequest grant_type(String grant_type) {
      this.grant_type = grant_type;
      return this;
    }

    public OAuthTokenRequest redirect_uri(String redirect_uri) {
      this.redirect_uri = redirect_uri;
      return this;
    }

    @Override
    public String toString() {
      return new StringJoiner(
        ", ",
        OAuthTokenRequest.class.getSimpleName() + "[",
        "]")
        .add("code='" + mask(code) + "'")
        .add("client_id='" + client_id + "'")
        .add("client_secret='" + mask(client_secret) + "'")
        .add("grant_type='" + grant_type + "'")
        .add("redirect_uri='" + redirect_uri + "'")
        .toString();
    }
  }
  
}
/*
 Example id_token payload:
{
  "iss": "https://accounts.google.com",
  "azp": "112489799301-m39l17uigum61l64uakb32vjhujuuk73.apps.googleusercontent.com",
  "aud": "112489799301-m39l17uigum61l64uakb32vjhujuuk73.apps.googleusercontent.com",
  "sub": "11...092",
  "hd": "ardc.edu.au",
  "email": "first.last@ardc.edu.au",
  "email_verified": true,
  "at_hash": "6eq...vTg",
  "iat": 1661572510,
  "exp": 1661576110
} 
 */