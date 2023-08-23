package au.org.raid.inttest.export;

import au.org.raid.api.util.Log;
import au.org.raid.idl.raidv2.model.RaidoMetadataSchemaV1;
import au.org.raid.inttest.IntegrationTestCase;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import raido.cmdline.AgencyPublicDataExport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static au.org.raid.api.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;
import static au.org.raid.api.test.util.BddUtil.*;
import static au.org.raid.api.util.IdeUtil.formatClickable;
import static au.org.raid.api.util.IoUtil.lines;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.idl.raidv2.model.AccessType.CLOSED;
import static au.org.raid.inttest.util.MinimalRaidTestData.createMinimalSchemaV1;
import static au.org.raid.inttest.util.MinimalRaidTestData.createMintRequest;
import static org.assertj.core.api.Assertions.assertThat;

/**
 The int-test superclass boots the api-svc in the usual way so we can call it
 and mint a raid to make sure there's at least one record to export.
 Then the test boots up the spring-context and runs the export bean method 
 directly.
 IMPROVE: we could launch it as a sub-process instead, in order to included
 the main method in the int-test.  Have to figure out how to pass params 
 if we do that - shouldn't be too hard, I'm jus being lazy.
 */
public class AgencyPublicDataExportTest extends IntegrationTestCase {
  private static final Log log = to(AgencyPublicDataExportTest.class);

  @Test
  @Disabled("Only handles V1 raids")
  void apiGetExistingShouldSucceedWithAcceptJson() throws IOException {
    var raidApi = super.basicRaidExperimentalClient();
    String openTitle = "open-"+ getName() + idFactory.generateUniqueId();
    String closedTitle = "closed-"+ getName() + idFactory.generateUniqueId();
    Path testFilePath = Files.createTempFile(getName(), ".ndjson");
    
    GIVEN("open and closed raids exist");
    var openRaidMint = raidApi.mintRaidoSchemaV1(
      createMintRequest(createMinimalSchemaV1(openTitle), RAIDO_SP_ID) );
    RaidoMetadataSchemaV1 closedMintData = createMinimalSchemaV1(closedTitle);
    closedMintData.getAccess().setType(CLOSED);
    closedMintData.getAccess().setAccessStatement("closed for int test");
    var closedRaidMint = raidApi.mintRaidoSchemaV1(
      createMintRequest(closedMintData, RAIDO_SP_ID) );

    WHEN("export is run");
    /* only need to do a few records to make sure it works */
    AgencyPublicDataExport.export(testFilePath.toString(), 10);
    
    THEN("file containing that raid should be written");
    /* given the data export is in descending date order, our minted raids
    should be the most recent things in here (might contain only our created 
    raids, or all of prod data or anything in between.) */
    String closedRaidJson =  lines(testFilePath).findFirst().orElse(null);
    String openRaidJson =  lines(testFilePath).skip(1).findFirst().orElse(null);
    
    log.info("test file: " + formatClickable(testFilePath));
    log.info("first line: " + closedRaidJson);
    log.info("second line: " + openRaidJson);

    /* I've gone for stringly-based stuff for the moment, but that's  very
    sensitive to details of the ObjectMapper - like order of the properties, or
    any spacing that gets applied.
    We could parse the JSON, but I also think we should be formally specifying
    property order and spacing anyway. */
    assertThat(openRaidJson).contains(
      "{\"title\":\"%s\",\"type\":\"Primary Title\",".formatted(openTitle) );
    assertThat(closedRaidJson).doesNotContain(
      "{\"type\":\"Primary Title\"" );
    assertThat(closedRaidJson).contains(
      "\"access\":{\"type\":\"Closed\"," +
        "\"accessStatement\":\"closed for int test\"}" );
  }

}