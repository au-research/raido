package au.org.raid.api.spring;

import au.org.raid.api.util.Log;
import org.springframework.context.ApplicationListener;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.stereotype.Component;

import static au.org.raid.api.util.Log.to;
import static java.util.Optional.ofNullable;

@Component
public class AuthorizationFailureListener
        implements ApplicationListener<AuthorizationFailureEvent> {
    private static final Log log = to(AuthorizationFailureListener.class);

    public AuthorizationFailureListener() {
        log.debug("ctor()");
    }

    @Override
    public void onApplicationEvent(AuthorizationFailureEvent event) {
        if (!log.isInfoEnabled()) {
            return;
        }

        var ex = event.getAccessDeniedException();
        var msg = log.with("exMessage", ex.getMessage());
        msg.with("source", event.getSource());

        // not sure if auth can be null?
        ofNullable(event.getAuthentication()).ifPresentOrElse(
                auth -> msg.with("principal", auth.getPrincipal()).
                        with("authorities", auth.getAuthorities()).
                        with("details", auth.getDetails()),
                () -> msg.with("auth", "[not set]")
        );

        // might be authn or authz that fails, anything we return a 403 etc. from
        msg.with("config", event.getConfigAttributes()).
                info("auth failed");
    }
}