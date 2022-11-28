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
import raido.apisvc.spring.config.environment.OrcidOidcProps;
import raido.apisvc.spring.config.environment.RaidoAuthnProps;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;

import static raido.apisvc.spring.security.IdProviderException.idpException;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.trimEqualsIgnoreCase;

@Component
public class OrcidOidc {
  private static final Log log = to(OrcidOidc.class);
  
  private OrcidOidcProps orcid;
  
  private RaidoAuthnProps raido;

  private RestTemplate rest;

  public OrcidOidc(
    OrcidOidcProps orcid, 
    RaidoAuthnProps raido,
    RestTemplate rest
  ) {
    this.orcid = orcid;
    this.raido = raido;
    this.rest = rest;
  }

  public boolean canHandle(String clientId){
    return trimEqualsIgnoreCase(clientId, orcid.clientId);    
  }
  
  public DecodedJWT exchangeCodeForVerifiedJwt(String idpResponseCode){
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    var tokenRequest = new OAuthTokenRequest().
      code(idpResponseCode).
      client_id(orcid.clientId).
      client_secret(orcid.clientSecret).
      grant_type("authorization_code").
      redirect_uri(raido.serverRedirectUri);

    HttpEntity<OAuthTokenRequest> request =
      new HttpEntity<>(tokenRequest, headers);

    log.with("bod", request.getBody()).info();

    ResponseEntity<OAuthTokenResponse> response = rest.postForEntity(
      orcid.tokenUrl, request, OAuthTokenResponse.class);


    log.with("response", response).
      with("response.body", response.getBody()).
      debug("orcid response");
    Guard.notNull(response.getBody());

    DecodedJWT jwt = JWT.decode(response.getBody().id_token);
    verify(jwt);
    
    return jwt;
  }
  
  public void verify(DecodedJWT jwt){
    Guard.areEqual(jwt.getAlgorithm(), "RS256");
    Guard.areEqual(jwt.getType(), "JWT");

    /* Probably overkill and unnecessary. If the attacker can intercept calls
    between api-svc and orcid to feed us a fake JWT, then they can intercept 
    the JWKS url call too.
    Keep an eye out for this when load testing; if measurably visible, 
    consider getting rid of this. */
    verifyOrcidJwksSignature(jwt);

    Guard.hasValue(jwt.getSubject());
    Guard.hasValue("id_token must have audience", jwt.getAudience());
    Guard.areEqual(orcid.clientId, jwt.getAudience().get(0));
    Guard.areEqual(orcid.issuer, jwt.getIssuer());
    
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

  private void verifyOrcidJwksSignature(DecodedJWT jwt) {
    JwkProvider provider = null;
    try {
      provider = new UrlJwkProvider( new URL(orcid.jwks) );
    }
    catch( MalformedURLException e ){
      throw idpException("orcid props.jwks is malformed %s - %s",
        orcid.jwks, e.getMessage() );
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
        jwt.getKeyId(), orcid.jwks, e.getMessage() );
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

}
