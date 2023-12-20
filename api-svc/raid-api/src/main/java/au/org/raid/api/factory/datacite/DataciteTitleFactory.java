package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteTitle;
import au.org.raid.idl.raidv2.model.Title;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataciteTitleFactory {

    public DataciteTitle create(final Title raidTitle) {
        if (raidTitle == null) {
            return null;
        }

        String title = Optional.ofNullable(raidTitle.getText()).orElse("");
        String startDate = Optional.ofNullable(raidTitle.getStartDate()).orElse("tba");
        String endDate = Optional.ofNullable(raidTitle.getEndDate()).orElse("tba");

        String formattedTitle = String.format("%s (%s through %s)", title, startDate, endDate);
        String titleType = (raidTitle.getType() != null && raidTitle.getType().getId().contains("alternative")) ? "AlternativeTitle" : null;

        return new DataciteTitle()
                .setDataciteTitle(formattedTitle)
                .setTitleType(titleType);
    }
}
