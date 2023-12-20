package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteDate;
import au.org.raid.idl.raidv2.model.Date;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataciteDateFactory {

    public DataciteDate create(final Date raidDate) {
        return Optional.ofNullable(raidDate)
                .map(date -> new DataciteDate()
                        .setDate(Optional.ofNullable(date.getStartDate()).orElse(""))
                        .setDateType("Available"))
                .orElse(null);
    }
}
