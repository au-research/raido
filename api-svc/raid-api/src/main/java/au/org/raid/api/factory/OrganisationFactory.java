package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.Organisation;
import au.org.raid.idl.raidv2.model.OrganisationRoleWithSchemaUri;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrganisationFactory {
    public Organisation create(final String pid, final String schemaUri, final List<OrganisationRoleWithSchemaUri> roles) {
        return new Organisation()
                .id(pid)
                .schemaUri(schemaUri)
                .role(roles);
    }
}