package raido.apisvc.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.Organisation;
import raido.idl.raidv2.model.OrganisationBlock;
import raido.idl.raidv2.model.OrganisationRoleWithSchemeUri;

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