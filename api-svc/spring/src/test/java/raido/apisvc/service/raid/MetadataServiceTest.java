package raido.apisvc.service.raid;

import org.junit.jupiter.api.Test;
import raido.apisvc.spring.config.environment.MetadataProps;
import raido.db.jooq.api_svc.tables.records.ServicePointRecord;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class MetadataServiceTest {
  private final MetadataProps metadataProps = new MetadataProps();

  private final MetadataService metadataService = new MetadataService(metadataProps);
  @Test
  void createIdBlock() {
    final var servicePointId = 999L;
    final var identifierOwner = "identifier-owner";
    final var identifierRegistrationAgency = "registration-agency";
    final var handle = "test-handle";
    final var identifierSchemeUri = "https://raid.org";

    metadataProps.raidAgencyIdentifier = identifierRegistrationAgency;

    final var servicePointRecord = new ServicePointRecord()
      .setId(servicePointId)
      .setIdentifierOwner(identifierOwner);

    final var idBlock = metadataService.createIdBlock(handle, servicePointRecord);

    assertThat(idBlock.getIdentifier(), is(handle));
    assertThat(idBlock.getIdentifierSchemeURI(), is(identifierSchemeUri));
    assertThat(idBlock.getIdentifierServicePoint(), is(servicePointId));
    assertThat(idBlock.getIdentifierOwner(), is(identifierOwner));
    assertThat(idBlock.getIdentifierRegistrationAgency(), is(identifierRegistrationAgency));

  }
}