package au.org.raid.iam.provider.cors;

import jakarta.ws.rs.ext.Provider;
import org.keycloak.services.resource.RealmResourceProvider;

@Provider
public class CorsResponseFilterResourceProvider implements RealmResourceProvider {
    @Override
    public Object getResource() {
        return new CorsResponseFilterResourceProvider();
    }

    @Override
    public void close() { }
}
