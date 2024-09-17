package au.org.raid.iam.provider.raid;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.crypto.KeyUse;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.cors.Cors;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

@Slf4j
@Provider
public class RaidPermissionsController {
    private static final String OPERATOR_ROLE_NAME = "operator";
    private static final String GROUP_ADMIN_ROLE_NAME = "group-admin";
    private static final String SERVICE_POINT_USER_ROLE = "service-point-user";
    private final AuthenticationManager.AuthResult auth;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final KeycloakSession session;
    public RaidPermissionsController(final KeycloakSession session) {
        this.session = session;
        this.auth = new AppAuthManager.BearerTokenAuthenticator(session).authenticate();
    }

    private Cors addCorsHeaders(final String... allowedMethods) {
        log.debug("Calling Cors");
        final var cors = session.getProvider(Cors.class);

        cors.allowedOrigins(
                "http://localhost:7080",
                "https://app.test.raid.org.au",
                "https://app3.test.raid.org.au",
                "https://app.demo.raid.org.au",
                "https://app.stage.raid.org.au",
                "https://app.prod.raid.org.au");

        cors.allowedMethods(allowedMethods);
        cors.auth();

        return cors;
    }

    @OPTIONS
    @Path("/raid-user")
    public Response addRaidUserPreflight() {
        return Response.fromResponse(addCorsHeaders("POST")
                        .preflight()
                        .builder(Response.ok())
                        .build())
                .build();
    }

    @POST
    @Path("/raid-user")
    @SneakyThrows
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addRaidUser(RaidUserPermissionsRequest request) {
        if (this.auth == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        final var currentUser = auth.getSession().getUser();

        if (currentUser.getRoleMappingsStream().anyMatch(role -> role.getName().equals("raid-admin"))) {
            if (currentUser.getAttributeStream("adminRaids").anyMatch(name -> name.equals(request.getHandle()))) {
                addUserToRaid(request.getUserId(), request.getHandle(), "userRaids", "raid-user");
            }
        } else if (currentUser.getRoleMappingsStream().anyMatch(role -> role.getName().equals("service-point-user"))) {
            final var raidClient = new RaidClient(objectMapper);
            final var key = session.keys().getActiveKey(session.getContext().getRealm(), KeyUse.SIG, "RS256");

            final var permissions = raidClient.getPermissions(request.getHandle(), auth.getToken(), key);
            if (permissions.isServicePointMatch()) {
                addUserToRaid(request.getUserId(), request.getHandle(), "userRaids", "raid-user");
            }
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return Response.fromResponse(
                        addCorsHeaders("POST")
                                .builder(Response.ok())
                                .build()
                )
                .entity("{}")
                .build();
    }

    @OPTIONS
    @Path("/raid-admin")
    public Response addRaidAdminPreflight() {
        return Response.fromResponse(addCorsHeaders("POST")
                        .preflight()
                        .builder(Response.ok())
                        .build())
                .build();
    }


    @POST
    @Path("/raid-admin")
    @SneakyThrows
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addRaidAdmin(RaidUserPermissionsRequest request) {
        if (this.auth == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        final var currentUser = auth.getSession().getUser();

        if (currentUser.getRoleMappingsStream().anyMatch(role -> role.getName().equals("service-point-user"))) {
            final var role = auth.getSession().getRealm().getRole("raid-admin");
            if (role == null) {
                throw new IllegalStateException("'raid-admin' role not found");
            }
            final var user = session.users().getUserById(auth.getSession().getRealm(), request.getUserId());

            user.grantRole(role);
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return Response.fromResponse(
                        addCorsHeaders("POST")
                                .builder(Response.ok())
                                .build()
                )
                .entity("{}")
                .build();
    }

    private void addUserToRaid(final String userId, final String handle, final String attributeName, final String roleName) {
        final var user = session.users().getUserById(auth.getSession().getRealm(), userId);
        final var userRaids = user.getAttributeStream(attributeName).collect(Collectors.toCollection(HashSet::new));
        userRaids.add(handle);
        user.setAttribute(attributeName, new ArrayList<>(userRaids));

        final var role = auth.getSession().getRealm().getRole(roleName);
        if (role == null) {
            throw new IllegalStateException("'%s' role not found".formatted(roleName));
        }

        user.grantRole(role);
    }


}
