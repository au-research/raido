package au.org.raid.api.spring.config.environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AafOidcProps {

    @Value("${AafOidc.clientId:accaabfd-a7c8-4d36-9363-ea7342e24db5}")
    public String clientId;

    @Value("${AafOidc.clientSecret}")
    public String clientSecret;

    @Value("${AafOidc.tokenUrl:" +
            "https://central.test.aaf.edu.au/providers/op/token}")
    public String tokenUrl;

    /**
     * The issuer that JWTs should be generated with.
     */
    @Value("${AafOidc.issuer:https://central.test.aaf.edu.au}")
    public String issuer;

    /**
     * The url where the IdP publishes its JWT public keys.
     * https://auth0.com/docs/secure/tokens/json-web-tokens/json-web-key-sets
     */
    @Value("${AafOidc.jwks:https://central.test.aaf.edu.au/providers/op/jwks}")
    public String jwks;

}
