package raido.inttest.endpoint.raidv2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import raido.apisvc.service.raid.id.IdentifierParser;
import raido.idl.raidv2.model.*;
import raido.inttest.IntegrationTestCase;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;
import static raido.apisvc.util.test.BddUtil.EXPECT;
import static raido.idl.raidv2.model.AccessType.OPEN;
import static raido.idl.raidv2.model.ContributorIdentifierSchemeType.HTTPS_ORCID_ORG_;
import static raido.idl.raidv2.model.ContributorPositionRaidMetadataSchemaType.LEADER;
import static raido.idl.raidv2.model.ContributorPositionSchemeType.HTTPS_RAID_ORG_;
import static raido.idl.raidv2.model.ContributorRoleCreditNisoOrgType.PROJECT_ADMINISTRATION;
import static raido.idl.raidv2.model.ContributorRoleSchemeType.HTTPS_CREDIT_NISO_ORG_;
import static raido.idl.raidv2.model.DescriptionType.PRIMARY_DESCRIPTION;
import static raido.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;
import static raido.idl.raidv2.model.TitleType.PRIMARY_TITLE;

public class OrcidIntegrationTest extends IntegrationTestCase {

  @Test
  @DisplayName("Create a RAiD with X as the checksum in the ORCID")
  void xChecksumOrcid() {
    var raidApi = super.basicRaidExperimentalClient();

    String initialTitle = getClass().getSimpleName() + "." + getName() +
      idFactory.generateUniqueId();
    var today = LocalDate.now();
    var idParser = new IdentifierParser();

    EXPECT("minting a raid with minimal content should succeed");
    var mintResult = raidApi.mintRaidoSchemaV1(
      new MintRaidoSchemaV1Request()
        .mintRequest(new MintRaidoSchemaV1RequestMintRequest()
          .servicePointId(RAIDO_SP_ID))
        .metadata(new RaidoMetadataSchemaV1()
          .metadataSchema(RAIDOMETADATASCHEMAV1)
          .titles(List.of(new TitleBlock()
            .type(PRIMARY_TITLE)
            .title(initialTitle)
            .startDate(today)))
          .dates(new DatesBlock().startDate(today))
          .descriptions(List.of(new DescriptionBlock()
            .type(PRIMARY_DESCRIPTION)
            .description("stuff about the int test raid")))
          .contributors(List.of(
            new ContributorBlock()
              .id("https://orcid.org/0009-0001-8177-319X")
              .identifierSchemeUri(HTTPS_ORCID_ORG_)
              .positions(List.of(new ContributorPosition()
                .positionSchemaUri(HTTPS_RAID_ORG_)
                .position(LEADER)
                .startDate(today)))
              .roles(List.of(
                new ContributorRole().
                  roleSchemeUri(HTTPS_CREDIT_NISO_ORG_).
                  role(PROJECT_ADMINISTRATION)))


          )).organisations(List.of(createDummyOrganisation(today)))
          .access(new AccessBlock().type(OPEN))
        )
    );

    assertThat(mintResult.getSuccess()).isTrue();
  }
}
