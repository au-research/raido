package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.Date;
import au.org.raid.idl.raidv2.model.DatesBlock;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class DatesFactory {
    public Date create(final DatesBlock datesBlock) {
        if (datesBlock == null) {
            return null;
        }

        var startDate = datesBlock.getStartDate() != null ?
                datesBlock.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null;

        var endDate = datesBlock.getEndDate() != null ?
                datesBlock.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null;

        return new Date()
                .startDate(startDate)
                .endDate(endDate);
    }
}