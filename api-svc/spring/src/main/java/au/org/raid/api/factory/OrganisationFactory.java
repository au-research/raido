package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.Organisation;
import au.org.raid.idl.raidv2.model.OrganisationBlock;
import au.org.raid.idl.raidv2.model.OrganisationRoleWithSchemeUri;
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

        List<OrganisationRoleWithSchemeUri> roles = null;

        if (organisationBlock.getRoles() != null) {
            roles = organisationBlock.getRoles().stream()
                    .map(roleFactory::create)
                    .toList();
        }

        final var identifierSchemeUri = (organisationBlock.getIdentifierSchemeUri() != null) ?
                organisationBlock.getIdentifierSchemeUri().getValue() : null;

        return new Organisation()
                .id(organisationBlock.getId())
                .identifierSchemeUri(identifierSchemeUri)
                .roles(roles);
    }
}