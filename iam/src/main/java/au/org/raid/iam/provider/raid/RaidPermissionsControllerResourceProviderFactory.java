package au.org.raid.iam.provider.raid;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

public class RaidPermissionsControllerResourceProviderFactory implements RealmResourceProviderFactory {
    public static final String ID = "raid";

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new RaidPermissionsControllerResourceProvider(session);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void init(final Config.Scope config) {}

    @Override
    public void postInit(final KeycloakSessionFactory factory) {}

    @Override
    public void close() {}
}
