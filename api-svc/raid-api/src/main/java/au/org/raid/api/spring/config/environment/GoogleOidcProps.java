package au.org.raid.api.spring.config.environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GoogleOidcProps {

    // default value is the DEMO OAuth client on raid.services@ardc account
    @Value("${GoogleOidc.clientId:" +
            "333652356987-5gn0qr18uj9n8po8dedi22dpmode1ogh.apps.googleusercontent.com}")
    public String clientId;

    @Value("${GoogleOidc.clientSecret}")
    public String clientSecret;

    /**
     * The endpoint that /idpresponse calls to exchange the OAuth2 authorization
     * code for a JWT bearer token.
     */
    @Value("${GoogleOidc.tokenUrl:https://oauth2.googleapis.com/token}")
    public String tokenUrl;

    /**
     * The issuer that should beGoogle OIDC JWT bearer tokens should be generated
     * with.
     */
    @Value("${GoogleOidc.issuer:https://accounts.google.com}")
    public String issuer;

    /**
     * The url where Google publishes its JWT public keys.
     * https://auth0.com/docs/secure/tokens/json-web-tokens/json-web-key-sets
     */
    @Value("${GoogleOidc.jwks:https://www.googleapis.com/oauth2/v3/certs}")
    public String jwks;

}
