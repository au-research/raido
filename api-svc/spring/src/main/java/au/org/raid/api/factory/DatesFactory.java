package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.Dates;
import au.org.raid.idl.raidv2.model.DatesBlock;
import org.springframework.stereotype.Component;

@Component
public class DatesFactory {
    public Dates create(final DatesBlock datesBlock) {
        if (datesBlock == null) {
            return null;
        }

        return new Dates()
            .startDate(datesBlock.getStartDate())
            .endDate(datesBlock.getEndDate());
    }
}