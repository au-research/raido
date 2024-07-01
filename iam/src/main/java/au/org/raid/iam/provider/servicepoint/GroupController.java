package au.org.raid.iam.provider.servicepoint;

import au.org.raid.iam.provider.servicepoint.dto.Grant;
import au.org.raid.iam.provider.servicepoint.dto.GroupJoinRequest;
import au.org.raid.iam.provider.servicepoint.dto.SetActiveGroupRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.cors.Cors;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;

import javax.management.relation.RoleNotFoundException;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Provider
public class GroupController {
    private static final String OPERATOR_ROLE_NAME = "operator";
    private static final String GROUP_ADMIN_ROLE_NAME = "group-admin";
    private static final String SERVICE_POINT_USER_ROLE = "service-point-user";
    private final AuthenticationManager.AuthResult auth;
    private final ObjectMapper objectMapper = new ObjectMapper();


    private final KeycloakSession session;
    public GroupController(final KeycloakSession session) {
        this.session = session;
        this.auth = new AppAuthManager.BearerTokenAuthenticator(session).authenticate();
    }

    private Cors addCorsHeaders(final String... allowedMethods) {
        log.debug("Calling Cors");
        final var cors = session.getProvider(Cors.class);

        cors.allowedOrigins(
                "http://localhost:7080",
                "https://app.test.raid.org.au",
                "https://app.demo.raid.org.au",
                "https://app.stage.raid.org.au",
                "https://app.prod.raid.org.au");

        cors.allowedMethods(allowedMethods);
        cors.auth();

        return cors;
    }

    @OPTIONS
    @Path("/all")
    public Response getGroupsPreflight() {
        return Response.fromResponse(addCorsHeaders( "GET", "PUT", "OPTIONS")
                        .preflight()
                        .builder(Response.ok())
                        .build())
                .build();
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroups() throws JsonProcessingException {
        log.debug("Getting all groups");

        if (this.auth == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        final var user = auth.getSession().getUser();
        final var realm = session.getContext().getRealm();
        final var groups = session.groups().getGroupsStream(realm)
                .map(g -> {
                    final var map = new HashMap<String, Object>();
                    map.put("id", g.getId());
                    map.put("name", g.getName());
                    map.put("attributes", g.getAttributes());
                    return map;
                })
                .toList();

        if (user == null) {
            throw new NotAuthorizedException("Bearer");
        }


        final var responseBody = new HashMap<String, Object>();

        responseBody.put("groups", groups);

        return Response.fromResponse(addCorsHeaders("GET")
                        .builder(
                                Response.ok()
                                        .entity(objectMapper.writeValueAsString(responseBody))
                        )
                        .build())
                .build();
    }



    @OPTIONS
    @Path("")
    public Response preflight() {
        return Response.fromResponse(addCorsHeaders( "GET", "PUT", "OPTIONS")
                        .preflight()
                        .builder(Response.ok())
                        .build())
                .build();
    }

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@QueryParam("groupId") String groupId) throws JsonProcessingException {
        log.debug("Getting members of group");

        final var objectMapper = new ObjectMapper();

        if (this.auth == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        final var user = auth.getSession().getUser();
        final var realm = session.getContext().getRealm();

        if (user == null) {
            throw new NotAuthorizedException("Bearer");
        }

        // Check if the user is neither a group admin nor an operator.
        if (!isGroupAdmin(user) && !isOperator(user)){
            throw new NotAuthorizedException("Permission denied");
        }

        /* If the 'groupId' parameter is set and the user is an operator,
            set the group to the one specified by 'groupId'.
            Otherwise, set it to the group ID from the user's profile. */
        var group = (groupId != null && isOperator(user))
                ? session.groups().getGroupById(realm, groupId)
                : user.getGroupsStream().toList().get(0);

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

        return Response.fromResponse(addCorsHeaders("GET")
                        .builder(
                                Response.ok()
                                        .entity(objectMapper.writeValueAsString(responseBody))
                        )
                        .build())
                .build();
    }

    @OPTIONS
    @Path("/grant")
    public Response grantPreflight() {
        log.debug("Calling grant with OPTIONS method");
        return Response.fromResponse(addCorsHeaders("PUT")
                        .preflight()
                        .builder(Response.ok())
                        .build())
                .build();
    }

    @PUT
    @Path("/grant")
    @SneakyThrows
    @Consumes(MediaType.APPLICATION_JSON)
    public Response grant(final Grant grant) {
        // check permissions of admin user

        if (this.auth == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        final var user = auth.getSession().getUser();

        if (user == null) {
            throw new NotAuthorizedException("Bearer");
        }

        if (!isGroupAdmin(user) && !isOperator(user)){
            throw new NotAuthorizedException("Permission denied - not a group admin");
        }

        if (!isGroupMember(user, grant.getGroupId()) && !isOperator(user)){
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

        return Response.fromResponse(
                        addCorsHeaders("PUT")
                                .builder(Response.ok())
                                .build()
                )
                .entity("{}")
                .build();
    }

    @OPTIONS
    @Path("/revoke")
    public Response revokePreflight() {
        return Response.fromResponse(addCorsHeaders("PUT")
                        .preflight()
                        .builder(Response.ok())
                        .build())
                .build();
    }

    @PUT
    @Path("/revoke")
    @SneakyThrows
    @Consumes(MediaType.APPLICATION_JSON)
    public Response revoke(final Grant grant) {
        if (this.auth == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        final var user = auth.getSession().getUser();

        if (user == null) {
            throw new NotAuthorizedException("Bearer");
        }

        if (!isGroupAdmin(user) && !isOperator(user)){
            throw new NotAuthorizedException("Permission denied - not a group admin");
        }

        if (!isGroupMember(user, grant.getGroupId()) && !isOperator(user)){
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

        return Response.fromResponse(
                addCorsHeaders("PUT")
                        .builder(Response.ok())
                        .build()
                )
                .entity("{}")
                .build();
    }

    @OPTIONS
    @Path("/join")
    public Response joinPreflight() {
        return Response.fromResponse(addCorsHeaders("PUT")
                        .preflight()
                        .builder(Response.ok())
                        .build())
                .build();
    }

    @PUT
    @Path("/join")
    @SneakyThrows
    @Consumes(MediaType.APPLICATION_JSON)
    public Response join(GroupJoinRequest request) {
        if (this.auth == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        final var user = auth.getSession().getUser();
        user.joinGroup(session.groups().getGroupById(session.getContext().getRealm(), request.getGroupId()));
        return Response.fromResponse(
                addCorsHeaders("PUT")
                        .builder(Response.ok())
                        .build()
                )
                .entity("{}")
                .build();
    }

    @OPTIONS
    @Path("/active-group")
    public Response setActiveGroupPreflight() {
        return Response.fromResponse(addCorsHeaders("PUT")
                        .preflight()
                        .builder(Response.ok())
                        .build())
                .build();
    }

    @PUT
    @Path("/active-group")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setActiveGroup(SetActiveGroupRequest request) {
        if (this.auth == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        final var user = auth.getSession().getUser();

        final var userGroups = user.getGroupsStream().map(GroupModel::getId).toList();

        if (userGroups.contains(request.getActiveGroupId())) {
            user.setAttribute("activeGroupId", List.of(request.getActiveGroupId()));
            return Response.fromResponse(
                            addCorsHeaders("PUT")
                                    .builder(Response.ok())
                                    .build()
                    )
                    .entity("{}")
                    .build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @OPTIONS
    @Path("/user-groups")
    public Response userGroupsPreflight() {
        return Response.fromResponse(addCorsHeaders("GET")
                        .preflight()
                        .builder(Response.ok())
                        .build())
                .build();
    }

    @GET
    @Path("/user-groups")
    @SneakyThrows
    @Consumes(MediaType.APPLICATION_JSON)
    public Response userGroups() {
        if (this.auth == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        final var user = auth.getSession().getUser();

        final var userGroups = user.getGroupsStream().map(g -> new GroupDetails(g.getId(),g.getName())).toList();

        return Response.fromResponse(
                        addCorsHeaders("GET")
                                .builder(Response.ok())
                                .build()
                )
                .entity(objectMapper.writeValueAsString(userGroups))
                .build();
    }

    private record GroupDetails(String id, String name) {}
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

    private boolean isOperator(final UserModel user) {
        return !user.getRoleMappingsStream()
                .filter(r -> r.getName().equals(OPERATOR_ROLE_NAME))
                .toList().isEmpty();
    }
}
