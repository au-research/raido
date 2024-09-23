package au.org.raid.api.config.security;

import org.jooq.Require;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.function.Supplier;

@Require
public class ServicePointOwnerAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    @Override
    public AuthorizationDecision check(final Supplier<Authentication> authentication, final RequestAuthorizationContext object) {
        return null;
    }
}
