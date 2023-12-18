package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.Date;
import org.springframework.stereotype.Component;

@Component
public class DateFactory {
    public Date create(final String startDate, final String endDate) {
        return new Date()
                .startDate(startDate)
                .endDate(endDate);
    }
}
