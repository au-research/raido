package au.org.raid.iam.provider.servicepoint;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

public class GroupControllerResourceProviderFactory implements RealmResourceProviderFactory {
    public static final String ID = "group";

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new GroupControllerResourceProvider(session);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void init(final Config.Scope config) {

    }

    @Override
    public void postInit(final KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }
}
