package raido.inttest.endpoint.raidv2;

import org.junit.jupiter.api.Test;
import raido.idl.raidv2.model.AccessBlock;
import raido.idl.raidv2.model.CreateRaidV1Request;
import raido.idl.raidv2.model.DatesBlock;
import raido.inttest.IntegrationTestCase;
import raido.inttest.RaidApiValidationException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static raido.apisvc.service.stub.InMemoryStubTestData.NONEXISTENT_TEST_DOI;
import static raido.apisvc.service.stub.InMemoryStubTestData.NONEXISTENT_TEST_ORCID;
import static raido.apisvc.service.stub.InMemoryStubTestData.NONEXISTENT_TEST_ROR;
import static raido.apisvc.util.test.BddUtil.EXPECT;
import static raido.idl.raidv2.model.AccessType.OPEN;
import static raido.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;
import static raido.inttest.util.MinimalRaidTestData.contributors;
import static raido.inttest.util.MinimalRaidTestData.descriptions;
import static raido.inttest.util.MinimalRaidTestData.organisations;
import static raido.inttest.util.MinimalRaidTestData.relatedObjects;
import static raido.inttest.util.MinimalRaidTestData.titles;

public class InvalidPidTest extends IntegrationTestCase {

  @Test
  void mintWithNonExistentPidsShouldFail() {
    var raidApi = basicRaidStableClient();
    String initialTitle = getClass().getSimpleName() + "." + getName() +
      idFactory.generateUniqueId();
    var today = LocalDate.now();

    EXPECT("minting a raid with non-existent PIDs should fail");
    assertThatThrownBy(()->raidApi.createRaidV1(new CreateRaidV1Request().
      metadataSchema(RAIDOMETADATASCHEMAV1).
      titles(titles(initialTitle)).
      dates(new DatesBlock().startDate(today)).
      descriptions(descriptions("used for testing non-existent pids")).
      contributors(contributors(NONEXISTENT_TEST_ORCID)).
      organisations(organisations(NONEXISTENT_TEST_ROR)).
      relatedObjects(relatedObjects(NONEXISTENT_TEST_DOI)).
      access(new AccessBlock().type(OPEN))
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
}
