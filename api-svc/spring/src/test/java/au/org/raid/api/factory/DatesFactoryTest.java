package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.Dates;
import au.org.raid.idl.raidv2.model.DatesBlock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

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

        assertThat(result.getStartDate(), is(startDate));
        assertThat(result.getEndDate(), is(endDate));
    }

    @Test
    @DisplayName("Returns null if DatesBlock is null")
    void returnsNull() {
        assertThat(datesFactory.create(null), nullValue());
    }

    @Test
    @DisplayName("Dates fields are null if DatesBlock fields are null")
    void nullFields() {
        assertThat(datesFactory.create(new DatesBlock()), is(new Dates()));
    }
}