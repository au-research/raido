package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.Contributor;
import au.org.raid.idl.raidv2.model.ContributorBlock;
import au.org.raid.idl.raidv2.model.ContributorPositionWithSchemeUri;
import au.org.raid.idl.raidv2.model.ContributorRoleWithSchemeUri;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ContributorFactory {
    private final ContributorPositionFactory positionFactory;
    private final ContributorRoleFactory roleFactory;

    public Contributor create(final ContributorBlock contributorBlock) {
        if (contributorBlock == null) {
            return null;
        }

        List<ContributorPositionWithSchemeUri> positions = null;
        List<ContributorRoleWithSchemeUri> roles = null;

        if (contributorBlock.getPositions() != null) {
            positions = contributorBlock.getPositions().stream()
                .map(positionFactory::create)
                .toList();
        }
        if (contributorBlock.getRoles() != null) {
            roles = contributorBlock.getRoles().stream()
                .map(roleFactory::create)
                .toList();
        }

        final var identifierSchemeUri = (contributorBlock.getIdentifierSchemeUri() != null) ?
            contributorBlock.getIdentifierSchemeUri().getValue() : null;

        return new Contributor()
            .id(contributorBlock.getId())
            .identifierSchemeUri(identifierSchemeUri)
            .positions(positions)
            .roles(roles);


    }
}