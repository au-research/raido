package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.OrganisationRoleWithSchemaUri;
import org.springframework.stereotype.Component;

@Component
public class OrganisationRoleFactory {
    public OrganisationRoleWithSchemaUri create(final String id, final String schemaUri, final String startDate, final String endDate) {
        return new OrganisationRoleWithSchemaUri()
                .id(id)
                .schemaUri(schemaUri)
                .startDate(startDate)
                .endDate(endDate);
    }
}