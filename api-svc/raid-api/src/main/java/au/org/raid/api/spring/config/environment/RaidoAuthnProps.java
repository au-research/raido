package au.org.raid.api.spring.config.environment;

import au.org.raid.api.util.Guard;
import au.org.raid.api.util.Log;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.StringUtil.areEqual;

@Component
public class RaidoAuthnProps {
    private static final Log log = to(RaidoAuthnProps.class);

    /**
     * This the location of the endpoint where Raido does OIDC responses.
     * It's the Raido location where the IDP redirects the browser to after they
     * user has approved authentication.
     * IMPROVE: Should be able to infer this based on local knowledge of the
     * server/port we're running on instead of as a config item?
     */
    @Value("${RaidoAuthn.serverRedirectUri:http://localhost:8080/idpresponse}")
    public String serverRedirectUri;

    /**
     * These are the hosts that a client is allowed pass in the state param.
     * It's the place that the /idpresponse url  will redirect the client to after
     * successful authentication.
     * Needs to be renamed from `allowedRedirectUris`, to `allowedRedirectHosts` -
     * we want to allow any landing page to be navigated to even if the user had
     * to authenticate before we could let them get there.
     * Think about someone following a link to an auth-request that we email them:
     * if they're not signed in, we want them to sign-in (going through any number
     * of "chain of trust" OIDC pages) and then we want them to finally land on the
     * page that they were trying to get to.
     */
    @Value("${RaidoAuthn.allowedRedirectUris:" +
            "https://demo.raido-infra.com/,http://localhost:7080/,https://localhost:6080/}")
    public String allowedClientRedirectUriString;

    private List<String> allowedClientRedirectUris;

    @PostConstruct
    public void init() {
        this.allowedClientRedirectUris = Arrays.asList(
                allowedClientRedirectUriString.split(","));
        log.with("allowedClientRedirectUris", allowedClientRedirectUris).
                with("uriCount", allowedClientRedirectUris.size()).
                info("RaidoAuthnProps");
        allowedClientRedirectUris.forEach(i -> {
            Guard.isTrue(
                    "must start with http or https: %s".formatted(i),
                    i.toLowerCase().startsWith("http"));
            Guard.isTrue(
                    "must end with a slash: %s".formatted(i),
                    i.toLowerCase().endsWith("/"));
            Guard.isTrue(
                    "allowed urls should be lowercase: %s".formatted(i),
                    areEqual(i.toLowerCase(), i));
            Guard.isTrue(
                    "allowed urls should be not have whitespace: %s".formatted(i),
                    areEqual(i.trim(), i));

        });
    }

    public List<String> getAllowedClientRedirectUris() {
        return allowedClientRedirectUris;
    }
}
