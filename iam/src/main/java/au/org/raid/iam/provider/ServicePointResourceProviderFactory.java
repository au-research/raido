package au.org.raid.iam.provider;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

public class ServicePointResourceProviderFactory implements RealmResourceProviderFactory {
    public static final String ID = "group";

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new ServicePointResourceProvider(session);
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
