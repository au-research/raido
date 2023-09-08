package au.org.raid.api.model;

import java.time.LocalDate;

public interface DateRange {
    LocalDate getStartDate();

    LocalDate getEndDate();
}
