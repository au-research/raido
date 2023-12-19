package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteDto;
import au.org.raid.idl.raidv2.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.ArrayList;
import java.util.List;

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
    DataciteAlternateIdentifierFactory dataciteAlternateIdentifierFactory = new DataciteAlternateIdentifierFactory();
    private final DataciteAttributesDtoFactory dataciteAttributesDtoFactory = new DataciteAttributesDtoFactory(
            dataciteTitleFactory,
            dataciteCreatorFactory,
            dataciteDateFactory,
            dataciteContributorFactory,
            dataciteDescriptionFactory,
            dataciteRelatedIdentifierFactor,
            dataciteRightFactory,
            dataciteTypesFactory,
            dataciteAlternateIdentifierFactory
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

        AlternateIdentifier alternateIdentifier1 = new AlternateIdentifier();
        alternateIdentifier1.setId("https://doi.org/10.5281/zenodo.1234");
        alternateIdentifier1.setType("RAiD");

        raidUpdateRequest.setAlternateIdentifier(List.of(alternateIdentifier1));

        DataciteDto dataciteDto =  datacitePayloadFactory.payloadForUpdate(raidUpdateRequest, "10.82841/abc123");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dataciteDto);

        String expected = "{\n" +
                "  \"schemaVersion\" : \"http://datacite.org/schema/kernel-4\",\n" +
                "  \"type\" : \"dois\",\n" +
                "  \"attributes\" : {\n" +
                "    \"prefix\" : \"10.82841\",\n" +
                "    \"doi\" : \"10.82841/abc123\",\n" +
                "    \"publisher\" : \"ARDC\",\n" +
                "    \"publicationYear\" : \"2023\",\n" +
                "    \"creators\" : [ {\n" +
                "      \"dataciteCreator\" : \"https://ror.org/038sjwq14\",\n" +
                "      \"name\" : \"https://ror.org/038sjwq14\"\n" +
                "    } ],\n" +
                "    \"contributors\" : [ {\n" +
                "      \"name\" : \"Name for https://orcid.org/0009-0000-9306-3120\",\n" +
                "      \"contributorType\" : \"Researcher\"\n" +
                "    }, {\n" +
                "      \"name\" : \"Name for https://ror.org/03yrm5c26\",\n" +
                "      \"contributorType\" : \"ResearchGroup\"\n" +
                "    } ],\n" +
                "    \"descriptions\" : [ {\n" +
                "      \"description\" : \"Description 1\",\n" +
                "      \"descriptionType\" : \"Abstract\"\n" +
                "    }, {\n" +
                "      \"description\" : \"Description 2\",\n" +
                "      \"descriptionType\" : \"Abstract\"\n" +
                "    } ],\n" +
                "    \"relatedIdentifiers\" : [ {\n" +
                "      \"relatedIdentifier\" : \"https://doi.org/10.5281/zenodo.1234\",\n" +
                "      \"relatedIdentifierType\" : \"HasPart\"\n" +
                "    }, {\n" +
                "      \"relatedIdentifier\" : \"https://example.com/alternate-url-1\",\n" +
                "      \"relatedIdentifierType\" : \"IsSourceOf\"\n" +
                "    }, {\n" +
                "      \"relatedIdentifier\" : \"https://raid.org.au/raid/1234\",\n" +
                "      \"relatedIdentifierType\" : \"HasPart\"\n" +
                "    } ],\n" +
                "    \"rightsList\" : [ {\n" +
                "      \"rights\" : \"https://creativecommons.org/licenses/by/4.0\",\n" +
                "      \"rightsUri\" : \"https://creativecommons.org/licenses/by/4.0\"\n" +
                "    } ],\n" +
                "    \"titles\" : [ {\n" +
                "      \"title\" : \"Title 1 (11-2021 through 2021-12-31)\"\n" +
                "    }, {\n" +
                "      \"title\" : \"Title 2 (tba through tba)\",\n" +
                "      \"titleType\" : \"AlternativeTitle\"\n" +
                "    } ],\n" +
                "    \"dates\" : [ {\n" +
                "      \"date\" : \"2021-01-01\",\n" +
                "      \"dateType\" : \"Available\"\n" +
                "    } ],\n" +
                "    \"types\" : {\n" +
                "      \"resourceType\" : \"RAiD Project\",\n" +
                "      \"resourceTypeGeneral\" : \"Other\"\n" +
                "    },\n" +
                "    \"alternateIdentifiers\" : [ {\n" +
                "      \"alternateIdentifier\" : \"https://doi.org/10.5281/zenodo.1234\",\n" +
                "      \"alternateIdentifierType\" : \"RAiD\"\n" +
                "    } ]\n" +
                "  }\n" +
                "}";

        try {
            JSONAssert.assertEquals(expected, json, false);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
