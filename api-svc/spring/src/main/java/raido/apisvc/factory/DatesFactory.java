package raido.apisvc.factory;

import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.Dates;
import raido.idl.raidv2.model.DatesBlock;

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