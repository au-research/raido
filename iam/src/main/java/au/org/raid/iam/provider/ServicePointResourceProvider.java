package au.org.raid.iam.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.ext.Provider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;
@Provider
public class ServicePointResourceProvider implements RealmResourceProvider {
    private KeycloakSession session;
    private ObjectMapper objectMapper;

    public ServicePointResourceProvider(final KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Object getResource() {
        return new ServicePointController(session);
    }

    @Override
    public void close() {

    }
}
