package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.Organisation;
import au.org.raid.idl.raidv2.model.OrganisationBlock;
import au.org.raid.idl.raidv2.model.OrganisationRoleWithSchemaUri;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrganisationFactory {
    private final OrganisationRoleFactory roleFactory;

    public Organisation create(final OrganisationBlock organisationBlock) {
        if (organisationBlock == null) {
            return null;
        }

        List<OrganisationRoleWithSchemaUri> roles = null;

        if (organisationBlock.getRoles() != null) {
            roles = organisationBlock.getRoles().stream()
                    .map(roleFactory::create)
                    .toList();
        }

        final var identifierSchemeUri = (organisationBlock.getIdentifierSchemeUri() != null) ?
                organisationBlock.getIdentifierSchemeUri().getValue() : null;

        return new Organisation()
                .id(organisationBlock.getId())
                .schemaUri(identifierSchemeUri)
                .role(roles);
    }
}