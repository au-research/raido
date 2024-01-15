package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteDate;
import au.org.raid.idl.raidv2.model.Date;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataciteDateFactoryTest {

    private DataciteDateFactory dataciteDateFactory = new DataciteDateFactory();

    @Test
    public void testCreateDate(){
        Date date1 = new Date();
        date1.setStartDate("2023-05-23");

        DataciteDate dataciteDate = dataciteDateFactory.create(date1);

        assertEquals(dataciteDate.getDate(), "2023-05-23");
        assertEquals(dataciteDate.getDateType(), "Available");
    }



}
