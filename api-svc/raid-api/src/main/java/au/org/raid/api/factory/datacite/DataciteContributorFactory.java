package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteContributor;
import au.org.raid.api.model.datacite.DataciteTitle;
import au.org.raid.idl.raidv2.model.Contributor;
import au.org.raid.idl.raidv2.model.Organisation;
import au.org.raid.idl.raidv2.model.Title;
import org.springframework.stereotype.Component;

@Component
public class DataciteContributorFactory {
    public DataciteContributor create(final Contributor raidContributor){
        if (raidContributor == null) {
            return null;
        }

        DataciteContributor dataciteContributorResult;

        String raidContributorId = (raidContributor.getId() != null) ? raidContributor.getId() : "";
        String raidContributorName = (raidContributor.getId() != null) ? String.format("Name for %s", raidContributorId) : "";

        dataciteContributorResult = new DataciteContributor()
                .dataciteContributor(raidContributorName)
                .contributorType("Researcher");

        return dataciteContributorResult;
    }

    public DataciteContributor create(final Organisation raidOrganisation){
        if (raidOrganisation == null) {
            return null;
        }

        DataciteContributor dataciteContributorResult;

        String raidContributorId = (raidOrganisation.getId() != null) ? raidOrganisation.getId() : "";
        String raidContributorName = (raidOrganisation.getId() != null) ? String.format("Name for %s", raidContributorId) : "";

        dataciteContributorResult = new DataciteContributor()
                .dataciteContributor(raidContributorName)
                .contributorType("ResearchGroup");

        return dataciteContributorResult;
    }
}
