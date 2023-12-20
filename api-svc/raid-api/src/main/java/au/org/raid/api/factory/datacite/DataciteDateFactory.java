package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteDate;
import au.org.raid.api.model.datacite.DataciteTitle;
import au.org.raid.idl.raidv2.model.Date;
import au.org.raid.idl.raidv2.model.Title;
import org.springframework.stereotype.Component;

@Component
public class DataciteDateFactory {
    public DataciteDate create(final Date raidDate){
        if (raidDate == null) {
            return null;
        }

        DataciteDate dataciteDateResult;

        String startDate = (raidDate.getStartDate() != null) ? raidDate.getStartDate() : "";

        dataciteDateResult = new DataciteDate().setDate(startDate).setDateType("Available");

        return dataciteDateResult;
    }
}
