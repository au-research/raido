package au.org.raid.iam.provider.raid;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.cors.Cors;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;

import java.util.stream.Collectors;

@Slf4j
@Provider
public class RaidPermissionsController {
    private final AuthenticationManager.AuthResult auth;
    private final KeycloakSession session;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    @POST
    @Path("/grant-user")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response grantUser(RaidPermissionsDto raidPermissions) {
        if (this.auth == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        final var user = auth.getSession().getUser();
        //TODO: Check user permissions

        final var raidUser = session.users().getUserById(auth.getSession().getRealm(), raidPermissions.getUserId());

        final var existingPermissions = raidUser.getAttributeStream("userRaids").collect(Collectors.toSet());

        existingPermissions.add(raidPermissions.getHandle());




//        raidUser.setAttribute("adminRaids", new ArrayList<>(raidPermissions.getAdminRaids()));
//        raidUser.setAttribute("userRaids", new ArrayList<>(raidPermissions.getUserRaids()));

        return Response.fromResponse(
                        addCorsHeaders("POST")
                                .builder(Response.ok())
                                .build()
                )
                .entity(raidPermissions)
                .build();
    }

//    @GET
//    @Path("/{userId}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response findByUserId(@PathParam("userId") final String userId) {
//        if (this.auth == null) {
//            return Response.status(Response.Status.UNAUTHORIZED).build();
//        }
//        final var user = auth.getSession().getUser();
//
//        final var raidUser = session.users().getUserById(auth.getSession().getRealm(), userId);
//
//        final var userRaids = raidUser.getAttributeStream("userRaids").collect(Collectors.toSet());
//        final var adminRaids = raidUser.getAttributeStream("adminRaids").collect(Collectors.toSet());
//
//        return Response.fromResponse(
//                        addCorsHeaders("GET")
//                                .builder(Response.ok())
//                                .build()
//                )
//                .entity(RaidPermissionsDto.builder()
//                        .userId(userId)
//                        .userRaids(userRaids)
//                        .adminRaids(adminRaids)
//                        .build()
//                )
//                .build();
//    }

}
