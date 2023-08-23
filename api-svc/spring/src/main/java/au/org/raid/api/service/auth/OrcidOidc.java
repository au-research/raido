package au.org.raid.api.service.auth;

import au.org.raid.api.spring.config.environment.OrcidOidcProps;
import au.org.raid.api.spring.config.environment.RaidoAuthnProps;
import au.org.raid.api.util.Guard;
import au.org.raid.api.util.Log;
import com.auth0.jwk.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;

import static au.org.raid.api.spring.security.IdProviderException.idpException;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.StringUtil.trimEqualsIgnoreCase;

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
  
  public DecodedJWT exchangeOAuthCodeForVerifiedIdToken(String idpResponseCode){
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("code", idpResponseCode);
    map.add("client_id", orcid.clientId);
    map.add("client_secret", orcid.clientSecret);
    map.add("grant_type", "authorization_code");
    map.add("redirect_uri", raido.serverRedirectUri);

    HttpEntity<MultiValueMap<String, String>> request =
      new HttpEntity<>(map, headers);
    
    // do not log this because it contains the client_secret
    // log.with("bod", request.getBody()).debug();

    ResponseEntity<OAuthTokenResponse> response = rest.postForEntity(
      orcid.tokenUrl, request, OAuthTokenResponse.class);


    // OAuthTokenResponse has a custom toString() to mask sensitive values 
    log.with("response", response).
      with("response.body", response.getBody()).
      debug("orcid response");
    Guard.notNull(response.getBody());

    DecodedJWT jwt = JWT.decode(response.getBody().id_token);
    log.with("jwt", jwt.getClaims()).debug();
    verify(jwt);
    
    return jwt;
  }
  
  public void verify(DecodedJWT jwt){
    Guard.areEqual(jwt.getAlgorithm(), "RS256");
    
    // orcid id_token does not have a type, see comment at end of file
    // Guard.areEqual(jwt.getType(), "bearer");

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
    
    /* Orcid allows users to choose to not make their email public. 
    Guard.hasValue("email claim must have value", 
      jwt.getClaim("email").asString());
    
    Guard.isTrue("email_verified must be true", 
      jwt.getClaim("email_verified").asBoolean());
    */
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

/*
- orcid uses the orcid id as the sub, seems dangerous
- no email because I did not have it public
- name fields were returned because I had it public
id_token: jwt={
  at_hash="UDS4BWIO0XKVVnJAr36Q1w", 
  sub="0000-0001-7233-7241", 
  iss="https://orcid.org", 
  given_name="Shorn", 
  aud="APP-IZBIZ6O7XH9RFG0X", 
  auth_time=1669694767, 
  exp=1669781174, 
  iat=1669694774, 
  family_name="Tolley", 
  jti="fe7bcdba-66bb-44c0-a6b8-d1b7383ac585"
  } 
 */