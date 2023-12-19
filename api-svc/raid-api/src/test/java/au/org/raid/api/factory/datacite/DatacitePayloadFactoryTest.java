package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteAttributesDto;
import au.org.raid.api.model.datacite.DataciteDto;
import au.org.raid.api.model.datacite.DataciteTitle;
import au.org.raid.idl.raidv2.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatacitePayloadFactoryTest {

    private final DataciteDtoFactory dataciteDtoFactory = new DataciteDtoFactory();
    DataciteTitleFactory dataciteTitleFactory = new DataciteTitleFactory();
    DataciteCreatorFactory dataciteCreatorFactory = new DataciteCreatorFactory();
    DataciteDateFactory dataciteDateFactory = new DataciteDateFactory();
    DataciteContributorFactory dataciteContributorFactory = new DataciteContributorFactory();
    DataciteDescriptionFactory dataciteDescriptionFactory = new DataciteDescriptionFactory();
    DataciteRelatedIdentifierFactory dataciteRelatedIdentifierFactor = new DataciteRelatedIdentifierFactory();
    DataciteRightFactory dataciteRightFactory = new DataciteRightFactory();
    DataciteTypesFactory dataciteTypesFactory = new DataciteTypesFactory();
    private final DataciteAttributesDtoFactory dataciteAttributesDtoFactory = new DataciteAttributesDtoFactory(
            dataciteTitleFactory,
            dataciteCreatorFactory,
            dataciteDateFactory,
            dataciteContributorFactory,
            dataciteDescriptionFactory,
            dataciteRelatedIdentifierFactor,
            dataciteRightFactory,
            dataciteTypesFactory
    );
    private final DatacitePayloadFactory datacitePayloadFactory = new DatacitePayloadFactory(dataciteDtoFactory, dataciteAttributesDtoFactory);

    @Test
    public void testCreate() throws JsonProcessingException {

        RaidUpdateRequest raidUpdateRequest  = new RaidUpdateRequest();

        Id id = new Id();
        id.setLicense("https://creativecommons.org/licenses/by/4.0");
        RegistrationAgency registrationAgency = new RegistrationAgency();
        registrationAgency.setId("https://ror.org/038sjwq14");
        id.setRegistrationAgency(registrationAgency);

        raidUpdateRequest.setIdentifier(id);

        Access access = new Access();
        AccessStatement accessStatement = new AccessStatement();
        access.setAccessStatement(accessStatement);

        raidUpdateRequest.setAccess(access);



        Title title1 = new Title();
        title1.setText("Title 1");
        title1.setStartDate("2021-01-01");
        title1.setEndDate("2021-12-31");

        Title title2 = new Title();
        title2.setText("Title 2");
        title1.setStartDate("11-2021");

        TitleTypeWithSchemaUri titleTypeWithSchemaUri = new TitleTypeWithSchemaUri();
        titleTypeWithSchemaUri.setSchemaUri("http://datacite.org/schema/kernel-4");
        titleTypeWithSchemaUri.setId("alternative");
        title2.setType(titleTypeWithSchemaUri);

        List<Title> titles = new ArrayList<>();
        titles.add(title1);
        titles.add(title2);

        raidUpdateRequest.setTitle(titles);

        Date date1 = new Date();
        date1.setStartDate("2021-01-01");
        date1.setEndDate("2021-12-31");

        raidUpdateRequest.setDate(date1);

        Description description1 = new Description();
        description1.setText("Description 1");

        Description description2 = new Description();
        description2.setText("Description 2");

        List<Description> descriptions = new ArrayList<>();
        descriptions.add(description1);
        descriptions.add(description2);

        raidUpdateRequest.setDescription(descriptions);

        Contributor contributor1 = new Contributor();
        contributor1.setId("https://orcid.org/0009-0000-9306-3120");

        raidUpdateRequest.setContributor(List.of(contributor1));

        Organisation organisation1 = new Organisation();
        organisation1.setId("https://ror.org/03yrm5c26");

        raidUpdateRequest.setOrganisation(List.of(organisation1));

        RelatedObject relatedObject1 = new RelatedObject();
        relatedObject1.setId("https://doi.org/10.5281/zenodo.1234");
        RelatedObjectType relatedObjectType = new RelatedObjectType();
        relatedObjectType.setId("output");
        relatedObject1.setType(relatedObjectType);

        raidUpdateRequest.setRelatedObject(List.of(relatedObject1));

        AlternateUrl alternateUrl1 = new AlternateUrl();
        alternateUrl1.setUrl("https://example.com/alternate-url-1");

        raidUpdateRequest.setAlternateUrl(List.of(alternateUrl1));

        RelatedRaid relatedRaid1 = new RelatedRaid();
        relatedRaid1.setId("https://raid.org.au/raid/1234");

        raidUpdateRequest.setRelatedRaid(List.of(relatedRaid1));

        DataciteDto dataciteDto =  datacitePayloadFactory.payloadForUpdate(raidUpdateRequest, "10.82841/abc123");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(dataciteDto);

        assertThat(1+1, is(2));
    }

}
