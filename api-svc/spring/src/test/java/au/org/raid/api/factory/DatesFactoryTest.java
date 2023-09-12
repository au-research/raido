package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.Date;
import au.org.raid.idl.raidv2.model.DatesBlock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class DatesFactoryTest {

    private final DatesFactory datesFactory = new DatesFactory();

    @Test
    @DisplayName("All fields are set")
    void allFieldsSet() {
        final var startDate = LocalDate.now().minusYears(2);
        final var endDate = LocalDate.now().minusYears(1);

        final var datesBlock = new DatesBlock().startDate(startDate).endDate(endDate);

        final var result = datesFactory.create(datesBlock);

        assertThat(result.getStartDate(), is(startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)));
        assertThat(result.getEndDate(), is(endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)));
    }

    @Test
    @DisplayName("Returns null if DatesBlock is null")
    void returnsNull() {
        assertThat(datesFactory.create(null), nullValue());
    }

    @Test
    @DisplayName("Dates fields are null if DatesBlock fields are null")
    void nullFields() {
        assertThat(datesFactory.create(new DatesBlock()), is(new Date()));
    }
}