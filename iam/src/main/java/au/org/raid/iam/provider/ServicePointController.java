package au.org.raid.iam.provider;

import au.org.raid.iam.provider.dto.Grant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Provider
public class ServicePointController {

    private static final String SERVICE_POINT_ACCESS_GRANTED_ATTRIBUTE_NAME = "servicePointAccessGranted";
    private static final String SERVICE_POINT_ID_ATTRIBUTE_NAME = "servicePointId";
    private final AuthenticationManager.AuthResult auth;

    private final KeycloakSession session;
    public ServicePointController(final KeycloakSession session) {
        this.session = session;
        this.auth = new AppAuthManager.BearerTokenAuthenticator(session).authenticate();
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

        final var responseBody = new HashMap<String, Object>();

        final var adminGroups = user.getAttributeStream("servicePointAdmin").toList();

        final var groups = user.getGroupsStream()
                .filter(g -> adminGroups.contains(g.getId()))
                .toList();

        final var servicePoints = new ArrayList<Map<String, Object>>();

        for (final var group : groups) {
            final var servicePoint = new HashMap<String, Object>();
            servicePoint.put("id", group.getId());
            servicePoint.put("name", group.getName());
            servicePoint.put("attributes", group.getAttributes());

            final var members = session.users().getGroupMembersStream(realm, group)
                    .filter(u -> !u.getId().equals(user.getId()))
                    .map(u -> {
                        final var map = new HashMap<String, Object>();
                        map.put("id", u.getId());
                        map.put("attributes", u.getAttributes());
                        return map;
                    })
                    .toList();

            servicePoint.put("members", members);
            servicePoints.add(servicePoint);
        }

        responseBody.put("servicePoints", servicePoints);

        return Response.ok().entity(objectMapper.writeValueAsString(responseBody)).build();
    }

    @PUT
    @Path("/grant")
    public Response grant(final Grant grant) throws JsonProcessingException {
        // check permissions of admin user
        final var user = auth.getSession().getUser();

        if (user == null) {
            throw new NotAuthorizedException("Bearer");
        }

        final var userAdminGroups = user.getAttributeStream("servicePointAdmin").toList();

        if (!userAdminGroups.contains(grant.getGroupId())) {
            throw new NotAuthorizedException("User does not have admin permission for group");
        }

        final var objectMapper = new ObjectMapper();

        final var realm = session.getContext().getRealm();

        final var groupUser = session.users().getUserById(realm, grant.getUserId());
        final var group = session.groups().getGroupById(realm, grant.getGroupId());

        final var grants =
                new ArrayList<>(groupUser.getAttributeStream(SERVICE_POINT_ACCESS_GRANTED_ATTRIBUTE_NAME).toList());
        grants.addAll(group.getAttributeStream(SERVICE_POINT_ID_ATTRIBUTE_NAME).toList());

        groupUser.setAttribute(SERVICE_POINT_ACCESS_GRANTED_ATTRIBUTE_NAME, grants);

        return Response.ok().entity(objectMapper.writeValueAsString(grants)).build();
    }

    @PUT
    @Path("/revoke")
    public Response revoke(final Grant grant) throws JsonProcessingException {
        // check permissions of admin user
        final var user = auth.getSession().getUser();

        if (user == null) {
            throw new NotAuthorizedException("Bearer");
        }

        final var userAdminGroups = user.getAttributeStream("servicePointAdmin").toList();

        if (!userAdminGroups.contains(grant.getGroupId())) {
            throw new NotAuthorizedException("User does not have admin permission for group");
        }

        final var objectMapper = new ObjectMapper();

        final var realm = session.getContext().getRealm();

        final var groupUser = session.users().getUserById(realm, grant.getUserId());
        final var group = session.groups().getGroupById(realm, grant.getGroupId());

        final var grants =
                new ArrayList<>(groupUser.getAttributeStream(SERVICE_POINT_ACCESS_GRANTED_ATTRIBUTE_NAME).toList());

        final var servicePointId = group.getFirstAttribute(SERVICE_POINT_ID_ATTRIBUTE_NAME);

        grants.remove(servicePointId);

        groupUser.setAttribute(SERVICE_POINT_ACCESS_GRANTED_ATTRIBUTE_NAME, grants);

        return Response.ok().entity(objectMapper.writeValueAsString(grants)).build();
    }
}
