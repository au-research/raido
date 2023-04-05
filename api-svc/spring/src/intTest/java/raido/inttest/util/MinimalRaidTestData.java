package raido.inttest.util;

import raido.idl.raidv2.model.AccessBlock;
import raido.idl.raidv2.model.DatesBlock;
import raido.idl.raidv2.model.DescriptionBlock;
import raido.idl.raidv2.model.MintRaidoSchemaV1Request;
import raido.idl.raidv2.model.MintRaidoSchemaV1RequestMintRequest;
import raido.idl.raidv2.model.RaidoMetadataSchemaV1;
import raido.idl.raidv2.model.TitleBlock;

import java.time.LocalDate;
import java.util.List;

import static raido.idl.raidv2.model.AccessType.OPEN;
import static raido.idl.raidv2.model.DescriptionType.PRIMARY_DESCRIPTION;
import static raido.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;
import static raido.idl.raidv2.model.TitleType.PRIMARY_TITLE;
import static raido.inttest.endpoint.raidv2.RaidoSchemaV1Test.createDummyLeaderContributor;

public class MinimalRaidTestData {
  /* lowest orcid withing the 0000-0001 range, with check digit.
   * verified doesn't exist (or at least, non-public):
   * https://orcid.org/0000-0001-0000-0009 */
  public static String DUMMY_ORCID = "https://orcid.org/0000-0003-0635-1998";
  public static String DUMMY_ROR = "https://ror.org/123xx4567";

  public static RaidoMetadataSchemaV1 createMinimalSchemaV1(String title) {
    var today = LocalDate.now();

    return new RaidoMetadataSchemaV1().
      metadataSchema(RAIDOMETADATASCHEMAV1).
      titles(List.of(new TitleBlock().
        type(PRIMARY_TITLE).
        title(title).
        startDate(today))).
      dates(new DatesBlock().startDate(today)).
      descriptions(List.of(new DescriptionBlock().
        type(PRIMARY_DESCRIPTION).
        description("stuff about the int test raid"))).
      contributors(List.of(createDummyLeaderContributor(today))).
      access(new AccessBlock().type(OPEN));
  }

  public static MintRaidoSchemaV1Request createMintRequest(
    RaidoMetadataSchemaV1 metadata,
    long servicePointId
  ) {
    return new MintRaidoSchemaV1Request().
      mintRequest(new MintRaidoSchemaV1RequestMintRequest().
        servicePointId(servicePointId)).
      metadata(metadata);
  }

}
