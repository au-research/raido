package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteContributor;
import au.org.raid.idl.raidv2.model.Contributor;
import au.org.raid.idl.raidv2.model.Organisation;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataciteContributorFactory {

    public DataciteContributor create(final Contributor raidContributor) {
        return Optional.ofNullable(raidContributor)
                .map(rc -> {
                    DataciteContributor dataciteContributor = new DataciteContributor();
                    dataciteContributor.setContributor(getContributorName(rc.getId()));
                    dataciteContributor.setContributorType("Researcher");
                    return dataciteContributor;
                })
                .orElse(null);
    }

    public DataciteContributor create(final Organisation raidOrganisation) {
        return Optional.ofNullable(raidOrganisation)
                .map(ro -> {
                    DataciteContributor dataciteContributor = new DataciteContributor();
                    dataciteContributor.setContributor(getContributorName(ro.getId()));
                    dataciteContributor.setContributorType("ResearchGroup");
                    return dataciteContributor;
                })
                .orElse(null);
    }

    private String getContributorName(String id) {
        return Optional.ofNullable(id)
                .map(i -> String.format("Name for %s", i))
                .orElse("");
    }
}