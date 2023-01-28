package raido.apisvc.endpoint.raidv2;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import raido.apisvc.service.raid.RaidService;
import raido.apisvc.spring.security.raidv2.AuthzTokenPayload;
import raido.idl.raidv2.model.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig(BasicRaidStableTest.Config.class)
class BasicRaidStableTest {

  private MockMvc mockMvc;

  @Autowired
  private RaidService raidService;


  @BeforeEach
  void setup(WebApplicationContext context) {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  @Test
  void readRaidV1_ReturnsOk() throws Exception {
    final var startDate = LocalDate.now().minusYears(1);
    final var endDate = startDate.plusMonths(6);
    final var title = "test-title";
    final var handle = "test-handle";
    final Long servicePointId = 123L;
    final var raid = createRaid(handle, servicePointId, title, startDate);

    final AuthzTokenPayload authzTokenPayload = mock(AuthzTokenPayload.class);
    when(authzTokenPayload.getServicePointId()).thenReturn(servicePointId);

    try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
      authzUtil.when(AuthzUtil::getAuthzPayload).thenReturn(authzTokenPayload);

      when(raidService.readRaidV1(handle)).thenReturn(raid);

      mockMvc.perform(get(String.format("/raid/v1/%s", handle))
          .characterEncoding("utf-8")
          .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.mintRequest.servicePointId", Matchers.is(servicePointId.intValue())))
        .andExpect(jsonPath("$.metadata.id.identifier", Matchers.is(handle)))
        .andExpect(jsonPath("$.metadata.id.identifierTypeUri", Matchers.is("https://raid.org")))
        .andExpect(jsonPath("$.metadata.id.globalUrl", Matchers.is("https://hdl.handle.net/" + handle)))
        .andExpect(jsonPath("$.metadata.id.raidAgencyUrl", Matchers.is("https://raid.org.au/handle/" + handle)))
        .andExpect(jsonPath("$.metadata.id.raidAgencyIdentifier", Matchers.is("raid.org.au")))
        .andExpect(jsonPath("$.metadata.titles[0].title", Matchers.is(title)))
        .andExpect(jsonPath("$.metadata.titles[0].type", Matchers.is(TitleType.PRIMARY_TITLE.getValue())))
        .andExpect(jsonPath("$.metadata.titles[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.titles[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.dates.startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.dates.endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.descriptions[0].description", Matchers.is("Test description...")))
        .andExpect(jsonPath("$.metadata.descriptions[0].type", Matchers.is("Primary Description")))
        .andExpect(jsonPath("$.metadata.access.type", Matchers.is("Open")))
        .andExpect(jsonPath("$.metadata.access.accessStatement", Matchers.is("Test access statement...")))
        .andExpect(jsonPath("$.metadata.contributors[0].id", Matchers.is("0000-0000-0000-0001")))
        .andExpect(jsonPath("$.metadata.contributors[0].identifierSchemeUri", Matchers.is("https://orcid.org/")))
        .andExpect(jsonPath("$.metadata.contributors[0].positions[0].positionSchemaUri", Matchers.is("https://raid.org/")))
        .andExpect(jsonPath("$.metadata.contributors[0].positions[0].position", Matchers.is("Leader")))
        .andExpect(jsonPath("$.metadata.contributors[0].positions[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.contributors[0].positions[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.contributors[0].roles[0].roleSchemeUri", Matchers.is("https://credit.niso.org/")))
        .andExpect(jsonPath("$.metadata.contributors[0].roles[0].role", Matchers.is("project-administration")))
        .andExpect(jsonPath("$.metadata.organisations[0].roles[0].role", Matchers.is("Lead Research Organisation")))
        .andExpect(jsonPath("$.metadata.organisations[0].roles[0].roleSchemeUri", Matchers.is("https://raid.org/")))
        .andExpect(jsonPath("$.metadata.organisations[0].roles[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.organisations[0].roles[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.organisations[0].id", Matchers.is("https://ror.org/038sjwq14")))
        .andExpect(jsonPath("$.metadata.organisations[0].identifierSchemeUri", Matchers.is("https://ror.org/")));

    }

  }


  private RaidSchemaV1 createRaid(final String handle, final long servicePointId, final String title, final LocalDate startDate) {
    final var endDate = startDate.plusMonths(6);
    final var idBlock = new IdBlock();
    idBlock.identifier(handle);
    idBlock.identifierTypeUri("https://raid.org");
    idBlock.globalUrl(String.format("https://hdl.handle.net/%s", handle));
    idBlock.raidAgencyUrl(String.format("https://raid.org.au/handle/%s", handle));
    idBlock.raidAgencyIdentifier("raid.org.au");

    final var titleBlock = new TitleBlock();
    titleBlock.setTitle(title);
    titleBlock.type(TitleType.PRIMARY_TITLE);
    titleBlock.startDate(startDate);
    titleBlock.endDate(endDate);

    final var datesBlock = new DatesBlock();
    datesBlock.startDate(startDate);
    datesBlock.endDate(endDate);

    final var descriptionBlock = new DescriptionBlock();
    descriptionBlock.description("Test description...");
    descriptionBlock.type(DescriptionType.PRIMARY_DESCRIPTION);

    final var accessBlock = new AccessBlock();
    accessBlock.type(AccessType.OPEN);
    accessBlock.accessStatement("Test access statement...");

    final var contributorPosition = new ContributorPosition();
    contributorPosition.position(ContributorPositionRaidMetadataSchemaType.LEADER);
    contributorPosition.positionSchemaUri(ContributorPositionSchemeType.HTTPS_RAID_ORG_);
    contributorPosition.startDate(startDate);
    contributorPosition.endDate(endDate);

    final var contributorRole = new ContributorRole();
    contributorRole.role(ContributorRoleCreditNisoOrgType.PROJECT_ADMINISTRATION);
    contributorRole.roleSchemeUri(ContributorRoleSchemeType.HTTPS_CREDIT_NISO_ORG_);

    final var contributorBlock = new ContributorBlock();
    contributorBlock.addPositionsItem(contributorPosition);
    contributorBlock.addRolesItem(contributorRole);
    contributorBlock.id("0000-0000-0000-0001");
    contributorBlock.identifierSchemeUri(ContributorIdentifierSchemeType.HTTPS_ORCID_ORG_);

    final var organisationRole = new OrganisationRole();
    organisationRole.startDate(startDate);
    organisationRole.endDate(endDate);
    organisationRole.role(OrganisationRoleType.LEAD_RESEARCH_ORGANISATION);
    organisationRole.roleSchemeUri(OrganisationRoleSchemeType.HTTPS_RAID_ORG_);

    final var organisationBlock = new OrganisationBlock();
    organisationBlock.id("https://ror.org/038sjwq14");
    organisationBlock.identifierSchemeUri(OrganisationIdentifierSchemeType.HTTPS_ROR_ORG_);
    organisationBlock.addRolesItem(organisationRole);

    final var raid = new RaidSchemaV1();

    final var mintRequest = new MintRequestSchemaV1();
    mintRequest.servicePointId(servicePointId);

    final var metadata = new MetadataSchemaV1();
    metadata.addTitlesItem(titleBlock);
    metadata.dates(datesBlock);
    metadata.addDescriptionsItem(descriptionBlock);
    metadata.access(accessBlock);
    metadata.addContributorsItem(contributorBlock);
    metadata.addOrganisationsItem(organisationBlock);

    metadata.setId(idBlock);
    raid.setMintRequest(mintRequest);
    raid.setMetadata(metadata);

    return raid;
  }

  @Configuration
  @EnableWebMvc
  @ComponentScan(basePackages = {
    // spring bootup and config
    "raido.apisvc.spring",
    // services and endpoints
    "raido.apisvc.service",
    "raido.apisvc.endpoint"
  })
  static class Config {
    @Bean
    RaidService raidService() {
      return mock(RaidService.class);
    }


  }
}