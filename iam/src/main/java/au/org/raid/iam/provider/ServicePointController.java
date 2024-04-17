package au.org.raid.iam.provider;

import au.org.raid.iam.provider.dto.Grant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lombok.SneakyThrows;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;

import javax.management.relation.RoleNotFoundException;
import java.util.HashMap;

@Provider
public class ServicePointController {
    private static final String GROUP_ADMIN_ROLE_NAME = "group-admin";
    private static final String SERVICE_POINT_USER_ROLE = "service-point-user";
    private static final String SERVICE_POINT_ID_ATTRIBUTE_NAME = "servicePointId";
    public static final String SERVICE_POINT_ADMIN_ATTRIBUTE_NAME = "servicePointAdmin";
    private final AuthenticationManager.AuthResult auth;

    private final KeycloakSession session;
    public ServicePointController(final KeycloakSession session) {
        this.session = session;
        this.auth = new AppAuthManager.BearerTokenAuthenticator(session).authenticate();
    }

    private static ObjectMapper objectMapper = new ObjectMapper();

    // Updated method to include an optional response body
    private static Response createCorsResponse(Object responseBody) throws JsonProcessingException {
        Response.ResponseBuilder responseBuilder = Response.ok();

        if (responseBody != null) {
            // Convert the responseBody to JSON string
            responseBuilder.entity(objectMapper.writeValueAsString(responseBody));
        }

        return responseBuilder
                .header("Access-Control-Allow-Origin", "https://app.demo.raid.org.au/")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .build();
    }

    @OPTIONS
    @Path("")
    public Response getOptions() throws JsonProcessingException {
        return createCorsResponse(null);
    }

    @OPTIONS
    @Path("/grant")
    public Response putGrantOptions() throws JsonProcessingException {
        return createCorsResponse(null);
    }

    @OPTIONS
    @Path("/revoke")
    public Response putRevokeOptions() throws JsonProcessingException {
        return createCorsResponse(null);
    }


    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() throws JsonProcessingException {

        final var objectMapper = new ObjectMapper();

        final var user = auth.getSession().getUser();
        final var realm = session.getContext().getRealm();

        if (user == null) {
            throw new NotAuthorizedException("Bearer");
        }

        if (!isGroupAdmin(user)){
            throw new NotAuthorizedException("Permission denied");
        }

        final var group = user.getGroupsStream().toList().get(0);
        final var responseBody = new HashMap<String, Object>();

        responseBody.put("id", group.getId());
        responseBody.put("name", group.getName());
        responseBody.put("attributes", group.getAttributes());

        final var members = session.users().getGroupMembersStream(realm, group)
                .filter(u -> !u.getId().equals(user.getId()))
                .map(u -> {
                    final var map = new HashMap<String, Object>();
                    map.put("id", u.getId());
                    map.put("attributes", u.getAttributes());
                    map.put("roles", u.getRoleMappingsStream().map(RoleModel::getName).toList());
                    return map;
                })
                .toList();

        responseBody.put("members", members);

        return createCorsResponse(objectMapper.writeValueAsString(responseBody));
    }

    @PUT
    @Path("/grant")
    @SneakyThrows
    @Consumes(MediaType.APPLICATION_JSON)
    public Response grant(final Grant grant) {
        // check permissions of admin user
        final var user = auth.getSession().getUser();

        if (user == null) {
            throw new NotAuthorizedException("Bearer");
        }

        if (!isGroupAdmin(user)){
            throw new NotAuthorizedException("Permission denied - not a group admin");
        }

        if (!isGroupMember(user, grant.getGroupId())){
            throw new NotAuthorizedException("Permission denied - not a group member");
        }

        final var realm = session.getContext().getRealm();

        final var groupUser = session.users().getUserById(realm, grant.getUserId());

        final var servicePointUserRole = session.roles()
                .getRealmRolesStream(realm, null, null)
                .filter(r -> r.getName().equals(SERVICE_POINT_USER_ROLE))
                .findFirst()
                .orElseThrow(() -> new RoleNotFoundException(SERVICE_POINT_USER_ROLE));

        groupUser.grantRole(servicePointUserRole);

        return createCorsResponse(objectMapper.writeValueAsString("{}"));
    }

    @PUT
    @Path("/revoke")
    @SneakyThrows
    @Consumes(MediaType.APPLICATION_JSON)
    public Response revoke(final Grant grant) throws JsonProcessingException {
        final var user = auth.getSession().getUser();

        if (user == null) {
            throw new NotAuthorizedException("Bearer");
        }

        if (!isGroupAdmin(user)){
            throw new NotAuthorizedException("Permission denied - not a group admin");
        }

        if (!isGroupMember(user, grant.getGroupId())){
            throw new NotAuthorizedException("Permission denied - not a group member");
        }

        final var realm = session.getContext().getRealm();

        final var groupUser = session.users().getUserById(realm, grant.getUserId());

        final var servicePointUserRole = session.roles()
                .getRealmRolesStream(realm, null, null)
                .filter(r -> r.getName().equals(SERVICE_POINT_USER_ROLE))
                .findFirst()
                .orElseThrow(() -> new RoleNotFoundException(SERVICE_POINT_USER_ROLE));

        groupUser.deleteRoleMapping(servicePointUserRole);

        return createCorsResponse(objectMapper.writeValueAsString("{}"));
    }

    private boolean isGroupAdmin(final UserModel user) {
        return !user.getRoleMappingsStream()
                .filter(r -> r.getName().equals(GROUP_ADMIN_ROLE_NAME))
                .toList().isEmpty();
    }

    private boolean isGroupMember(final UserModel user, final String groupId) {
        return !user.getGroupsStream()
                .filter(g -> g.getId().equals(groupId))
                .toList().isEmpty();
    }

}
