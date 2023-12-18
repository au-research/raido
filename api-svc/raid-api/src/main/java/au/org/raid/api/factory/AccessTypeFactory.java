package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.AccessTypeWithSchemaUri;
import org.springframework.stereotype.Component;

@Component
public class AccessTypeFactory {
    public AccessTypeWithSchemaUri create(final String id, final String schemaUri) {
        return new AccessTypeWithSchemaUri()
                .id(id)
                .schemaUri(schemaUri);
    }
}
