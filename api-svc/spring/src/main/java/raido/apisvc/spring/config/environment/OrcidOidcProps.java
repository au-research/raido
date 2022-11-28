package raido.apisvc.spring.config.environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// https://orcid.org/.well-known/openid-configuration
@Component
public class OrcidOidcProps {

  @Value("${OrcidOidc.clientId:APP-IZBIZ6O7XH9RFG0X}")
  public String clientId;

  @Value("${OrcidOidc.clientSecret}")
  public String clientSecret;

  /**
   The endpoint that /idpresponse calls to exchange the OAuth2 authorization
   code for a JWT bearer token.
   */
  @Value("${OrcidOidc.tokenUrl:https://orcid.org/oauth/token}")
  public String tokenUrl;

  /**
   The issuer that OIDC JWT bearer tokens should be generated with.
   */
  @Value("${OrcidOidc.issuer:https://orcid.org}")
  public String issuer;

  /**
   The url where the IDP publishes its JWT public keys.
   https://auth0.com/docs/secure/tokens/json-web-tokens/json-web-key-sets
   */
  @Value("${OrcidOidc.jwks:https://orcid.org/oauth/jwks}")
  public String jwks;

}
