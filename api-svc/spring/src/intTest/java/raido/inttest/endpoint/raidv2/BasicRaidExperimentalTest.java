package raido.inttest.endpoint.raidv2;

import org.junit.jupiter.api.Test;
import raido.idl.raidv2.model.MintRaidRequestV1;
import raido.idl.raidv2.model.RaidListItemV1;
import raido.idl.raidv2.model.RaidListRequest;
import raido.idl.raidv2.model.ReadRaidV1Request;
import raido.inttest.IntegrationTestCase;
import raido.inttest.util.IdFactory;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static raido.apisvc.endpoint.raidv2.BasicRaidExperimental.RAIDO_SP_ID;
import static raido.apisvc.util.test.BddUtil.EXPECT;
import static raido.apisvc.util.test.BddUtil.THEN;
import static raido.apisvc.util.test.BddUtil.WHEN;

public class BasicRaidExperimentalTest extends IntegrationTestCase {

  @Test
  void happyDayMintListRead() {
    var raidApi = super.basicRaidExperimentalClient();
    String initialName = "int test" + IdFactory.generateUniqueId();


    EXPECT("minting a raid with minimal content should succeed");
    var mintResult = raidApi.mintRaidV1(new MintRaidRequestV1().
      servicePointId(RAIDO_SP_ID).
      name(initialName));
    assertThat(mintResult).isNotNull();
    assertThat(mintResult.getHandle()).isNotBlank();
    assertThat(mintResult.getStartDate()).isNotNull();


    /* list by unique name to prevent eventual pagination issues */
    EXPECT("should be able to list the minted raid");
    var listResult = raidApi.listRaid(new RaidListRequest().
      servicePointId(RAIDO_SP_ID).name(initialName));
    assertThat(listResult).singleElement().satisfies(i->{
      assertThat(i.getHandle()).isEqualTo(mintResult.getHandle());
      assertThat(i.getName()).isEqualTo(initialName);
      assertThat(i.getStartDate()).isEqualTo(LocalDate.now());
    });


    EXPECT("should be able to read the minted raid");
    var readResult = raidApi.readRaidV1(new ReadRaidV1Request().
      handle(mintResult.getHandle()));
    assertThat(readResult).isNotNull();
    assertThat(readResult.getHandle()).isEqualTo(mintResult.getHandle());
    assertThat(readResult.getServicePointId()).isEqualTo(RAIDO_SP_ID);
    assertThat(readResult.getStartDate()).isNotNull();


    EXPECT("should be able to update the minted");
    String updatedName = readResult.getName() + " updated";
    var updateResult = raidApi.updateRaidV1(
      new MintRaidRequestV1().
        handle(readResult.getHandle()).
        name(updatedName).
        servicePointId(readResult.getServicePointId()).
        startDate(readResult.getStartDate())
    );
    assertThat(updateResult).isNotNull();
    assertThat(updateResult.getHandle()).isEqualTo(mintResult.getHandle());
    assertThat(updateResult.getStartDate()).
      isEqualTo(readResult.getStartDate());
    assertThat(updateResult.getName()).isEqualTo(updatedName);


    WHEN("list by initial name from before update");
    var listResult2 = raidApi.listRaid(new RaidListRequest().
      servicePointId(RAIDO_SP_ID).name(initialName));
    THEN("should find raid with updated name");
    assertThat(listResult2).singleElement().satisfies(i->{
      assertThat(i.getHandle()).isEqualTo(mintResult.getHandle());
      assertThat(i.getName()).isEqualTo(updatedName);
      assertThat(i.getStartDate()).isEqualTo(LocalDate.now());
    });


  }

}