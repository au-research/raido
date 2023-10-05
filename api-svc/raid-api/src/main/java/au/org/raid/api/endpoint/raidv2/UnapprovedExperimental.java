package au.org.raid.api.endpoint.raidv2;

import au.org.raid.api.service.auth.admin.AuthzRequestService;
import au.org.raid.api.spring.security.raidv2.UnapprovedUserApiToken;
import au.org.raid.api.util.ExceptionUtil;
import au.org.raid.api.util.Guard;
import au.org.raid.api.util.Log;
import au.org.raid.idl.raidv2.api.UnapprovedExperimentalApi;
import au.org.raid.idl.raidv2.model.UpdateAuthzRequest;
import au.org.raid.idl.raidv2.model.UpdateAuthzResponse;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

/**
 * "Unapproved" in that this class is about dealing with users who have been
 * authenticated by an IdProvider (google, etc.) but have yet been approved to
 * use the system for a particular service-point.
 */
@Scope(proxyMode = TARGET_CLASS)
@RestController
@Transactional
@CrossOrigin
public class UnapprovedExperimental implements UnapprovedExperimentalApi {
    private AuthzRequestService authzRequestSvc;

    public UnapprovedExperimental(
            AuthzRequestService authzRequestSvc
    ) {
        this.authzRequestSvc = authzRequestSvc;
    }

    /* inline authz code because there's only on endpoint, if you end up creating
    more "unapproved user" endpoints (what for?) factor out the SecurityContext
    code to AuthzUtil or somewhere. */
    @Override
    public ResponseEntity<UpdateAuthzResponse> updateAuthzRequest(UpdateAuthzRequest req) {
        Guard.notNull(req);
        Guard.notNull(req.getServicePointId());
        Guard.notNull("comments may be empty, but not null", req.getComments());

        var apiToken = getContext().getAuthentication();

        if (apiToken == null) {
            throw ExceptionUtil.authFailed();
        }

        if (!(apiToken instanceof UnapprovedUserApiToken unapprovedUserToken)) {
            throw ExceptionUtil.authFailed();
        }

        return ResponseEntity.ok(authzRequestSvc.updateRequestAuthz(
                unapprovedUserToken.getClientId(),
                unapprovedUserToken.getEmail(),
                unapprovedUserToken.getSubject(),
                req));
    }

}
