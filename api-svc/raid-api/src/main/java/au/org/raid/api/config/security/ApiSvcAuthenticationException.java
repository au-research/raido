package au.org.raid.api.config.security;

import org.springframework.security.core.AuthenticationException;

/*
Not sure what I'm doing with this yet, other than extending the spring AuthEx.
Thinking about an "internal/external" message split. 
 */
public class ApiSvcAuthenticationException
        extends AuthenticationException {
    /**
     * the exception message ends up being returned to client,
     * so don't put stuff in there.
     */
    public ApiSvcAuthenticationException() {
        super("authentication failed");
    }
}
