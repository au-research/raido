package au.org.raid.iam.provider.cors;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

public class CorsResponseFilterResourceProviderFactory implements RealmResourceProviderFactory {
    private static final String PROVIDER_ID = "raid-cors-response-filter";

    @Override
    public RealmResourceProvider create(final KeycloakSession session) {
        return new CorsResponseFilterResourceProvider();
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

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
