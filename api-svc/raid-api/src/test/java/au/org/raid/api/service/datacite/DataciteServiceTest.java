package au.org.raid.api.service.datacite;

import au.org.raid.api.factory.datacite.DataciteDtoFactory;
import au.org.raid.api.model.datacite.DataciteData;
import au.org.raid.api.model.datacite.DataciteDto;
import au.org.raid.api.spring.config.DataciteProperties;
import au.org.raid.idl.raidv2.model.RaidCreateRequest;
import au.org.raid.idl.raidv2.model.Title;
import au.org.raid.idl.raidv2.model.TitleType;
import au.org.raid.idl.raidv2.model.TitleTypeWithSchemaUri;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DataciteServiceTest {
    private String handle;
    private RaidCreateRequest createRequest;
    private final RestTemplate restTemplate = new RestTemplate();
    private final DataciteProperties properties = new DataciteProperties();

    private final DataciteService dataciteService = new DataciteService(properties);

    @BeforeEach
    public void createExampleRequest() {
        this.handle = "10.123/RAID-TEST";
        this.createRequest = new RaidCreateRequest();

        Title title1 = new Title();
        title1.setText("title1");
        title1.setStartDate("2020-01-01");
        title1.setEndDate("2020-12-31");
        TitleTypeWithSchemaUri titleTypeWithSchemaUri = new TitleTypeWithSchemaUri();
        titleTypeWithSchemaUri.setId("alternative");
        title1.setType(titleTypeWithSchemaUri);

        Title title2 = new Title();
        title2.setText("title2");


        this.createRequest.setTitle(List.of(title1, title2));
    }

    @Test
    public void testCreatePayloadForRequest() throws JsonProcessingException {

        DataciteDtoFactory dataciteDtoFactory = new DataciteDtoFactory();
        DataciteDto payloadForRequest = dataciteDtoFactory.create(createRequest, handle);

        DataciteData dataciteData = new DataciteData();
        dataciteData.setData(payloadForRequest);
        assertEquals(dataciteData.getData().getAttributes().getTitles().get(0).getDataciteTitle(), "title1 (2020-01-01 through 2020-12-31)");
    }
}
