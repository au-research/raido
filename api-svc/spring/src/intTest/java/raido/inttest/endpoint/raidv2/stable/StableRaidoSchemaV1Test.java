package raido.inttest.endpoint.raidv2.stable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import raido.apisvc.service.raid.id.IdentifierHandle;
import raido.idl.raidv2.model.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StableRaidoSchemaV1Test extends AbstractStableIntegrationTest {
  private static final String LEAD_RESEARCH_ORGANISATION =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/organisation/role/v1/lead-research-organisation.json";

  private static final String ORGANISATION_ROLE_SCHEME_URI =
    "https://github.com/au-research/raid-metadata/tree/main/scheme/organisation/role/v1";

  private static final String ORGANISATION_SCHEME_URI =
    "https://ror.org/";

  private static final String ACCESS_TYPE_OPEN =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/open.json";

  private static final String ACCESS_TYPE_SCHEME_URI =
    "https://github.com/au-research/raid-metadata/tree/main/scheme/access/type/v1";

  private static final String PRIMARY_TITLE_TYPE =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/primary.json";

  private static final String ALTERNATIVE_TITLE_TYPE =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/alternative.json";

  private static final String TITLE_TYPE_SCHEME_URI =
    "https://github.com/au-research/raid-metadata/tree/main/scheme/title/type/v1";

  private static final String PRIMARY_DESCRIPTION_TYPE =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/description/type/v1/primary.json";

  private static final String DESCRIPTION_TYPE_SCHEME_URI =
    "https://github.com/au-research/raid-metadata/tree/main/scheme/description/type/v1";

  private static final String CONTRIBUTOR_SCHEME_URI = "https://orcid.org/";

  private static final String CONTRIBUTOR_POSITION_SCHEME_URI =
    "https://github.com/au-research/raid-metadata/tree/main/scheme/contributor/position/v1";

  private static final String LEADER_POSITION =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/leader.json";

  private static final String CONTRIBUTOR_ROLE_SCHEME_URI = "https://credit.niso.org/";

  private static final String SOFTWARE_ROLE =
    "https://credit.niso.org/contributor-roles/software/";

  @Test
  @DisplayName("Mint a raid")
  void mintRaid() {
    final var mintedRaid = raidApi.createRaidV1(createRequest);

    final var path = URI.create(mintedRaid.getId().getIdentifier()).getPath();

    final var handle = (IdentifierHandle) identifierParser.parseHandle(path);
    final var result = raidApi.readRaidV1(handle.prefix(), handle.suffix());

    assertThat(result.getTitles()).isEqualTo(createRequest.getTitles());
    assertThat(result.getDescriptions()).isEqualTo(createRequest.getDescriptions());
    assertThat(result.getAccess()).isEqualTo(createRequest.getAccess());
    assertThat(result.getContributors()).isEqualTo(createRequest.getContributors());
    assertThat(result.getOrganisations()).isEqualTo(createRequest.getOrganisations());
    assertThat(result.getDates()).isEqualTo(createRequest.getDates());
  }

  @Test
  @DisplayName("Update a raid")
  void updateRaid() {
    final var mintedRaid = raidApi.createRaidV1(createRequest);

    final var path = URI.create(mintedRaid.getId().getIdentifier()).getPath();

    final var handle = (IdentifierHandle) identifierParser.parseHandle(path);
    final var result = raidApi.readRaidV1(handle.prefix(), handle.suffix());

    assertThat(result.getTitles()).isEqualTo(createRequest.getTitles());
    assertThat(result.getDescriptions()).isEqualTo(createRequest.getDescriptions());
    assertThat(result.getAccess()).isEqualTo(createRequest.getAccess());
    assertThat(result.getContributors()).isEqualTo(createRequest.getContributors());
    assertThat(result.getOrganisations()).isEqualTo(createRequest.getOrganisations());
    assertThat(result.getDates()).isEqualTo(createRequest.getDates());

  }
  
  private UpdateRaidV1Request mapReadToUpdate(RaidDto read){
    return new UpdateRaidV1Request().
      id(read.getId()).
      metadataSchema(read.getMetadataSchema()).
      titles(read.getTitles()).
      dates(read.getDates()).
      descriptions(read.getDescriptions()).
      access(read.getAccess()).
      alternateUrls(read.getAlternateUrls()).
      contributors(read.getContributors()).
      organisations(read.getOrganisations()).
      subjects(read.getSubjects()).
      relatedRaids(read.getRelatedRaids()).
      relatedObjects(read.getRelatedObjects()).
      alternateIdentifiers(read.getAlternateIdentifiers()).
      spatialCoverages(read.getSpatialCoverages()).
      traditionalKnowledgeLabels(read.getTraditionalKnowledgeLabels());
  }

  public Contributor contributor(
    final String orcid,
    final String position,
    String role,
    LocalDate startDate
  ) {
    return new Contributor()
      .id(orcid)
      .identifierSchemeUri(CONTRIBUTOR_SCHEME_URI)
      .positions(List.of(new ContribPosition()
        .schemeUri(CONTRIBUTOR_POSITION_SCHEME_URI)
        .type(position)
        .startDate(startDate)))
      .roles(List.of(
        new ContribRole()
          .schemeUri(CONTRIBUTOR_ROLE_SCHEME_URI)
          .type(role)));
  }

  public Organisation organisation(
    String ror,
    String role,
    LocalDate today
  ) {
    return new Organisation()
      .id(ror)
      .identifierSchemeUri(ORGANISATION_SCHEME_URI).
      roles(List.of(
        new OrgRole()
          .schemeUri(ORGANISATION_ROLE_SCHEME_URI)
          .type(role)
          .startDate(today)));
  }
}