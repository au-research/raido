package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteTitle;
import au.org.raid.idl.raidv2.model.Title;
import au.org.raid.idl.raidv2.model.TitleType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataciteTitleFactoryTest {

    private DataciteTitleFactory dataciteTitleFactory = new DataciteTitleFactory();

    @Test
    public void testCreateWithMinimumPayload(){
        Title title1 = new Title();
        title1.setText("Title 1");

        DataciteTitle dataciteTitle = dataciteTitleFactory.create(title1);

        assertEquals(dataciteTitle.getDataciteTitle(), "Title 1 (tba through tba)");
    }

    @Test
    public void testCreateWithAlternativeTitleType(){
        Title title1 = new Title();

        title1.setText("Title 1");

        TitleType titleType1 = new TitleType();
        titleType1.setId("alternative");
        title1.setType(titleType1);

        DataciteTitle dataciteTitle = dataciteTitleFactory.create(title1);

        assertEquals(dataciteTitle.getTitleType(), "AlternativeTitle");
    }

    @Test
    public void testCreateWithStart(){
        Title title1 = new Title();

        title1.setText("Title 1");

        title1.setStartDate("2020-01-01");

        DataciteTitle dataciteTitle = dataciteTitleFactory.create(title1);

        assertEquals(dataciteTitle.getDataciteTitle(), "Title 1 (2020-01-01 through tba)");
    }
    @Test
    public void testCreateWithStartAndEnd(){
        Title title1 = new Title();

        title1.setText("Title 1");

        title1.setStartDate("2020-01-01");
        title1.setEndDate("2020-12-31");

        DataciteTitle dataciteTitle = dataciteTitleFactory.create(title1);

        assertEquals(dataciteTitle.getDataciteTitle(), "Title 1 (2020-01-01 through 2020-12-31)");
    }

}
