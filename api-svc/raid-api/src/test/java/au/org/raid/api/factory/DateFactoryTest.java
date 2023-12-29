package au.org.raid.api.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DateFactoryTest {
    private final DateFactory factory = new DateFactory();

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var startDate = "2021";
        final var endDate = "2022";

        final var result = factory.create(startDate, endDate);

        assertThat(result.getStartDate(), is(startDate));
        assertThat(result.getEndDate(), is(endDate));
    }
}