package au.org.raid.iam.provider.raid;

import jakarta.ws.rs.ext.Provider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

@Provider
public class RaidPermissionsControllerResourceProvider implements RealmResourceProvider {
    private final KeycloakSession session;

    public RaidPermissionsControllerResourceProvider(final KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Object getResource() {
        return new RaidPermissionsController(session);
    }

    @Override
    public void close() {

    }

}
