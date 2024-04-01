package au.org.raid.api.factory.datacite;

import au.org.raid.idl.raidv2.model.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DataciteDateFactoryTest {

    private DataciteDateFactory dataciteDateFactory = new DataciteDateFactory();

    @Test
    @DisplayName("Create with start date only")
    public void startDateOnly(){
        final var start = "2023-05-23";

        final var date = new Date()
                .startDate(start);

        final var result = dataciteDateFactory.create(date);

        assertThat(result.getDate(), is("2023-05-23"));
        assertThat(result.getDateType(), is("Other"));
    }

    @Test
    @DisplayName("Create with start and end date")
    public void startAndEndDate(){
        final var start = "2023-05-23";
        final var end = "2023-06-23";

        final var date = new Date()
                .startDate(start)
                .endDate(end);

        final var result = dataciteDateFactory.create(date);

        assertThat(result.getDate(), is("2023-05-23/2023-06-23"));
        assertThat(result.getDateType(), is("Other"));
    }
}
