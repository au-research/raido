package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.ContributorRoleWithSchemaUri;
import org.springframework.stereotype.Component;

@Component
public class ContributorRoleFactory {
    public ContributorRoleWithSchemaUri create(final String id, final String schemaUri) {
        return new ContributorRoleWithSchemaUri()
                .id(id)
                .schemaUri(schemaUri);
    }
}