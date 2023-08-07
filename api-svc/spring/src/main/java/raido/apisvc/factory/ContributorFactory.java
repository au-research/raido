package raido.apisvc.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.Contributor;
import raido.idl.raidv2.model.ContributorBlock;
import raido.idl.raidv2.model.ContributorPositionWithSchemeUri;
import raido.idl.raidv2.model.ContributorRoleWithSchemeUri;

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