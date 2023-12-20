package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteTitle;
import au.org.raid.idl.raidv2.model.Title;
import org.springframework.stereotype.Component;

@Component
public class DataciteTitleFactory {
    public DataciteTitle create(final Title raidTitle){
        if (raidTitle == null) {
            return null;
        }

        DataciteTitle dataciteTitleResult;

        String title = (raidTitle.getText() != null) ? raidTitle.getText() : "";
        String startDate = (raidTitle.getStartDate() != null) ? raidTitle.getStartDate() : "tba";
        String endDate = (raidTitle.getEndDate() != null) ? raidTitle.getEndDate() : "tba";

        String result = String.format("%s (%s through %s)", title, startDate, endDate);

        if(raidTitle.getType() != null && raidTitle.getType().getId().contains("alternative")) {
            dataciteTitleResult = new DataciteTitle()
                    .setDataciteTitle(result)
                    .setTitleType("AlternativeTitle");
        } else {
            dataciteTitleResult = new DataciteTitle()
                    .setDataciteTitle(result);
        }

        return dataciteTitleResult;

    }
}
