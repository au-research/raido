package raido.inttest.endpoint.raidv2;

import org.junit.jupiter.api.Test;
import raido.idl.raidv2.model.*;
import raido.inttest.IntegrationTestCase;
import raido.inttest.RaidApiValidationException;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static raido.apisvc.service.stub.InMemoryStubTestData.*;
import static raido.apisvc.util.test.BddUtil.EXPECT;

public class InvalidPidTest extends IntegrationTestCase {
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

  private static final String TITLE_TYPE_SCHEME_URI =
    "https://github.com/au-research/raid-metadata/tree/main/scheme/title/type/v1";

  private static final String PRIMARY_DESCRIPTION_TYPE =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/description/type/v1/primary.json";

  private static final String DESCRIPTION_TYPE_SCHEME_URI =
    "https://github.com/au-research/raid-metadata/tree/main/scheme/description/type/v1";

  private static final String CONTRIBUTOR_SCHEME_URI =
    "https://orcid.org/";

  private static final String CONTRIBUTOR_POSITION_SCHEME_URI =
    "https://github.com/au-research/raid-metadata/tree/main/scheme/contributor/position/v1";

  private static final String LEADER_POSITION =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/leader.json";

  private static final String SUPERVISION_ROLE = "https://credit.niso.org/contributor-roles/supervision/";

  private static final String CONTRIBUTOR_ROLE_SCHEME_URI = "https://credit.niso.org/";

  @Test
  void mintWithNonExistentPidsShouldFail() {
    var raidApi = basicRaidStableClient();
    String initialTitle = getClass().getSimpleName() + "." + getName() +
      idFactory.generateUniqueId();
    var today = LocalDate.now();

    EXPECT("minting a raid with non-existent PIDs should fail");
    assertThatThrownBy(()->raidApi.createRaidV1(new CreateRaidV1Request().
      titles(titles(initialTitle)).
      dates(new DatesBlock().startDate(today)).
      descriptions(descriptions("used for testing non-existent pids")).
      contributors(contributors(NONEXISTENT_TEST_ORCID)).
      organisations(organisations(NONEXISTENT_TEST_ROR)).
      relatedObjects(relatedObjects(NONEXISTENT_TEST_DOI)).
      access(new Access()
        .type(new AccessType()
          .id(ACCESS_TYPE_OPEN)
          .schemeUri(ACCESS_TYPE_SCHEME_URI))
        )
    )).isInstanceOfSatisfying(RaidApiValidationException.class, ex->{
       assertThat(ex.getFailures()).anySatisfy(iFail->{
         assertThat(iFail.getFieldId()).isEqualTo("contributors[0].id");
         assertThat(iFail.getMessage()).contains("ORCID does not exist");
       }); 
       assertThat(ex.getFailures()).anySatisfy(iFail->{
         assertThat(iFail.getFieldId()).isEqualTo("organisations[0].id");
         assertThat(iFail.getMessage()).contains("ROR does not exist");
       }); 
       assertThat(ex.getFailures()).anySatisfy(iFail->{
         assertThat(iFail.getFieldId()).isEqualTo("relatedObjects[0].id");
         assertThat(iFail.getMessage()).contains("DOI does not exist");
       }); 
    });

  }

  public List<Title> titles(
    String title
  ){
    return List.of(new Title()
      .type(new TitleType()
        .id(PRIMARY_TITLE_TYPE)
        .schemeUri(TITLE_TYPE_SCHEME_URI)
      )
      .title(title)
      .startDate(LocalDate.now()));
  }

  public static List<Description> descriptions(String description){
    final var descriptionType = new DescriptionType()
      .id(PRIMARY_DESCRIPTION_TYPE)
      .schemeUri(DESCRIPTION_TYPE_SCHEME_URI);

    return List.of(new Description()
      .type(descriptionType)
      .description(description)
    );
  }

  public List<Contributor> contributors(
    String orcid
  ) {
    var today = LocalDate.now();
    return List.of(new Contributor()
      .id(orcid)
      .identifierSchemeUri(CONTRIBUTOR_SCHEME_URI)
      .positions(List.of(new ContributorPosition()
        .schemeUri(CONTRIBUTOR_POSITION_SCHEME_URI)
        .id(LEADER_POSITION)
        .startDate(today)))
      .roles(List.of(
        new ContributorRole()
          .schemeUri(CONTRIBUTOR_ROLE_SCHEME_URI)
          .id(SUPERVISION_ROLE))));
  }

  public List<Organisation> organisations(String ror){
    return List.of(organisation(ror, LEAD_RESEARCH_ORGANISATION, LocalDate.now()));
  }

  public Organisation organisation(
    String ror,
    String role,
    LocalDate today
  ) {
    return new Organisation()
      .id(ror)
      .identifierSchemeUri(ORGANISATION_SCHEME_URI)
      .roles(List.of(
        new OrganisationRole()
          .schemeUri(ORGANISATION_ROLE_SCHEME_URI)
          .id(role)
          .startDate(today)));
  }

  public List<RelatedObject> relatedObjects(String doi) {
    return List.of(relatedObject(doi, "conference-paper.json"));
  }
  public RelatedObject relatedObject(String doi, String type){
    return new RelatedObject()
      .id(doi)
      .identifierSchemeUri("https://doi.org/")
      .type(
        new RelatedObjectType()
          .id("https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/audiovisual.json")
          .schemeUri("https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/type/v1")
      )
      .category(
        new RelatedObjectCategory()
          .id("https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/category/v1/input.json")
          .schemeUri("https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/category/v1")
      );
  }
}