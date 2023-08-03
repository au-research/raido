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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import raido.apisvc.spring.config.environment.AafOidcProps;
import raido.apisvc.spring.config.environment.RaidoAuthnProps;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;

import static raido.apisvc.spring.security.IdProviderException.idpException;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.hasValue;
import static raido.apisvc.util.StringUtil.trimEqualsIgnoreCase;

@Component
public class AafOidc {
  private static final Log log = to(AafOidc.class);

  private AafOidcProps aaf;
  private RaidoAuthnProps raido;

  private RestTemplate rest;

  public AafOidc(
    AafOidcProps aaf,
    RaidoAuthnProps raido,
    RestTemplate rest
  ) {
    this.aaf = aaf;
    this.raido = raido;
    this.rest = rest;
  }

  public boolean canHandle(String clientId) {
    return trimEqualsIgnoreCase(clientId, aaf.clientId);
  }

  public DecodedJWT exchangeOAuthCodeForVerifiedIdToken(
    String idpResponseCode
  ) {
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

    ResponseEntity<OAuthTokenResponse> response = rest.postForEntity(
      aaf.tokenUrl, request, OAuthTokenResponse.class);

    // OAuthTokenResponse has a custom toString() to mask sensitive values 
    log.with("response", response).
      with("response.body", response.getBody()).
      debug("aaf response");
    Guard.notNull(response.getBody());

    DecodedJWT jwt = JWT.decode(response.getBody().id_token);
    
    verify(jwt);

    return jwt;
  }

  public void verify(DecodedJWT jwt) {
    Guard.areEqual(jwt.getAlgorithm(), "RS256");
    /* https://aaf.freshdesk.com/support/tickets/10432
    AAF test system stopped returning this field on 2023-05-23-ish, 
    the field is optional according to the standard. */
    if( hasValue(jwt.getType()) ){
      Guard.areEqual(jwt.getType(), "JWT");
    }

    verifyAafJwksSignature(jwt);

    Guard.hasValue(jwt.getSubject());
    Guard.hasValue("id_token must have audience", jwt.getAudience());
    Guard.areEqual(aaf.clientId, jwt.getAudience().get(0));
    Guard.areEqual(aaf.issuer, jwt.getIssuer());

    Guard.isTrue(
      "id_token must not be expired",
      jwt.getExpiresAtAsInstant().
        // add a bit of leeway
        plusMillis(1000).
        isAfter(Instant.now()) );

    Guard.hasValue(
      "email claim must have value",
      jwt.getClaim("email").asString());

    // AAF id_token does not contain an email_verified claim

  }

  private void verifyAafJwksSignature(DecodedJWT jwt) {
    JwkProvider provider = null;
    try {
      provider = new UrlJwkProvider(new URL(aaf.jwks));
    }
    catch( MalformedURLException e ){
      throw idpException("AAF props.jwks is malformed %s - %s",
        aaf.jwks, e.getMessage());
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
        jwt.getKeyId(), aaf.jwks, e.getMessage());
    }

    Algorithm algorithm = null;
    try {
      algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
    }
    catch( InvalidPublicKeyException e ){
      log.with("header", jwt.getHeader()).
        with("payload", jwt.getPayload()).
        debug("could not get jwks key");
      throw idpException(
        "could not create RSA256 public key - %s",
        e.getMessage());
    }

    algorithm.verify(jwt);
  }
}

