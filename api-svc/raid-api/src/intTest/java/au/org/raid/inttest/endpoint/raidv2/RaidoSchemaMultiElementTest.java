package au.org.raid.inttest.endpoint.raidv2;

import au.org.raid.idl.raidv2.model.*;
import au.org.raid.inttest.IntegrationTestCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static au.org.raid.api.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;
import static au.org.raid.idl.raidv2.model.AccessType.OPEN;
import static au.org.raid.idl.raidv2.model.DescriptionType.ALTERNATIVE_DESCRIPTION;
import static au.org.raid.idl.raidv2.model.DescriptionType.PRIMARY_DESCRIPTION;
import static au.org.raid.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;
import static au.org.raid.idl.raidv2.model.TitleType.ALTERNATIVE_TITLE;
import static au.org.raid.idl.raidv2.model.TitleType.PRIMARY_TITLE;
import static au.org.raid.inttest.endpoint.raidv2.RaidoSchemaV1Test.createDummyLeaderContributor;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Initially implemented so we could test the UI's logic for detecting complicated
 * metadata.  But it's worth having a test to show that we don't stomp extra
 * elements on the backend when we update either - so the update logic and
 * asserttions were added.
 */
public class RaidoSchemaMultiElementTest extends IntegrationTestCase {

    @Test
    void multipleTitleDescriptionRaid() throws JsonProcessingException {
        var raidApi = super.basicRaidExperimentalClient();
        String primaryTitle = getClass().getSimpleName() + "." + getName() +
                idFactory.generateUniqueId();
        var today = LocalDate.now();
        String alternateTitle = "alternate title";

        var mintResult = raidApi.mintRaidoSchemaV1(
                new MintRaidoSchemaV1Request().
                        mintRequest(new MintRaidoSchemaV1RequestMintRequest().
                                servicePointId(RAIDO_SP_ID)).
                        metadata(new RaidoMetadataSchemaV1().
                                metadataSchema(RAIDOMETADATASCHEMAV1).
                                titles(List.of(
                                        new TitleBlock().type(PRIMARY_TITLE).
                                                title(primaryTitle).startDate(today),
                                        new TitleBlock().type(ALTERNATIVE_TITLE).
                                                title(alternateTitle).startDate(today)
                                )).
                                dates(new DatesBlock().startDate(today)).
                                descriptions(List.of(
                                        new DescriptionBlock().
                                                type(PRIMARY_DESCRIPTION).
                                                description("stuff about the int test raid"),
                                        new DescriptionBlock().
                                                type(ALTERNATIVE_DESCRIPTION).
                                                description("alternate description")
                                )).
                                contributors(List.of(createDummyLeaderContributor(today))).
                                organisations(List.of(createDummyOrganisation(today))).
                                access(new AccessBlock().type(OPEN))
                        )
        ).getBody();


        var readRaid = raidApi.readRaidV2(
                new ReadRaidV2Request().handle(mintResult.getRaid().getHandle())).getBody();
        System.out.println("XXX: " + readRaid);
        var readMeta = mapper.readValue(
                readRaid.getMetadata().toString(), RaidoMetadataSchemaV1.class);
        // not actually sure about ordering of returned json elements?
        assertThat(readMeta.getTitles().get(0).getType()).isEqualTo(PRIMARY_TITLE);
        var readPrimaryTitle = readMeta.getTitles().get(0);
        TitleBlock updatedTitle =
                readPrimaryTitle.title(readPrimaryTitle.getTitle() + " updated");
        readMeta.getTitles().set(0, updatedTitle);

        var updateResult = raidApi.updateRaidoSchemaV1(
                new UpdateRaidoSchemaV1Request().metadata(
                        readMeta
                ));


        readRaid = raidApi.readRaidV2(
                new ReadRaidV2Request().handle(mintResult.getRaid().getHandle())).getBody();
        readMeta = mapper.readValue(
                readRaid.getMetadata().toString(), RaidoMetadataSchemaV1.class);

        // not actually sure about ordering of returned json elements?
        assertThat(readMeta.getTitles().get(0).getType()).
                isEqualTo(PRIMARY_TITLE);
        assertThat(readMeta.getTitles().get(0).getTitle()).
                isEqualTo(updatedTitle.getTitle());
        assertThat(readMeta.getTitles().get(1).getType()).
                isEqualTo(ALTERNATIVE_TITLE);
        assertThat(readMeta.getTitles().get(1).getTitle()).
                isEqualTo(alternateTitle);

    }
}