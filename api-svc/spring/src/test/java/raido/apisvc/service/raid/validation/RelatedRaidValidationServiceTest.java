package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.Test;
import raido.apisvc.repository.RaidRepository;
import raido.apisvc.repository.RelatedRaidTypeRepository;
import raido.apisvc.spring.config.environment.MetadataProps;
import raido.db.jooq.api_svc.tables.records.RaidRecord;
import raido.db.jooq.api_svc.tables.records.RelatedRaidTypeRecord;
import raido.idl.raidv2.model.RelatedRaidBlock;
import raido.idl.raidv2.model.ValidationFailure;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class RelatedRaidValidationServiceTest {

  private static final String RELATED_RAID_TYPE_SCHEME_URI =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/relationship-type";

  private final RelatedRaidTypeRepository relatedRaidTypeRepository = mock(RelatedRaidTypeRepository.class);

  private final RaidRepository raidRepository = mock(RaidRepository.class);

  private final MetadataProps metadataProps = new MetadataProps();

  private  final RelatedRaidValidationService validationService =
    new RelatedRaidValidationService(raidRepository, relatedRaidTypeRepository, metadataProps);

  @Test
  void addsValidationFailureIfRelatedRaidUrlIsIncorrect() {
    metadataProps.handleUrlPrefix = "handle-url-prefix";
    final var relatedRaidUrl = "example.com";
    final var relatedRaidType = RELATED_RAID_TYPE_SCHEME_URI + "/" + "continues.json";

    when(relatedRaidTypeRepository.findByUrl(relatedRaidType)).thenReturn(Optional.of(mock(RelatedRaidTypeRecord.class)));

    final var relatedRaid = new RelatedRaidBlock()
      .relatedRaid(relatedRaidUrl)
      .relatedRaidType(relatedRaidType)
      .relatedRaidTypeSchemeUri(RELATED_RAID_TYPE_SCHEME_URI);

    final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

    final var validationFailure = validationFailures.get(0);
    assertThat(validationFailures.size(), is(1));
    assertThat(validationFailure.getFieldId(), is("relatedRaids[0].relatedRaid"));
    assertThat(validationFailure.getErrorType(), is("invalid"));
    assertThat(validationFailure.getMessage(), is("RelatedRaid is invalid."));

    verifyNoInteractions(raidRepository);
  }

  @Test
  void addsValidationFailureIfRelatedRaidRaidIsNotFound() {
    metadataProps.handleUrlPrefix = "handle-url-prefix";
    final var handle = "123.45/67890";
    final var relatedRaidUrl = metadataProps.handleUrlPrefix + "/" + handle;
    final var relatedRaidType = RELATED_RAID_TYPE_SCHEME_URI + "/" + "continues.json";

    when(raidRepository.findByHandle(handle)).thenReturn(Optional.empty());
    when(relatedRaidTypeRepository.findByUrl(relatedRaidType)).thenReturn(Optional.of(mock(RelatedRaidTypeRecord.class)));

    final var relatedRaid = new RelatedRaidBlock()
      .relatedRaid(relatedRaidUrl)
      .relatedRaidType(relatedRaidType)
      .relatedRaidTypeSchemeUri(RELATED_RAID_TYPE_SCHEME_URI);

    final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

    final var validationFailure = validationFailures.get(0);
    assertThat(validationFailures.size(), is(1));
    assertThat(validationFailure.getFieldId(), is("relatedRaids[0].relatedRaid"));
    assertThat(validationFailure.getErrorType(), is("invalid"));
    assertThat(validationFailure.getMessage(), is("Related Raid was not found."));
  }

  @Test
  void noValidationFailuresWhenRelatedRaidIsFound() {
    metadataProps.handleUrlPrefix = "handle-url-prefix";
    final var handle = "123.45/67890";
    final var relatedRaidUrl = metadataProps.handleUrlPrefix + "/" + handle;
    final var relatedRaidType = RELATED_RAID_TYPE_SCHEME_URI + "/" + "continues.json";

    when(raidRepository.findByHandle(handle)).thenReturn(Optional.of(mock(RaidRecord.class)));
    when(relatedRaidTypeRepository.findByUrl(relatedRaidType)).thenReturn(Optional.of(mock(RelatedRaidTypeRecord.class)));

    final var relatedRaid = new RelatedRaidBlock()
      .relatedRaid(relatedRaidUrl)
      .relatedRaidType(relatedRaidType)
      .relatedRaidTypeSchemeUri(RELATED_RAID_TYPE_SCHEME_URI);

    final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

    assertThat(validationFailures, is(empty()));
  }

  @Test
  void noValidationFailuresRelatedRaidBlockIsNull() {
    final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(null);

    assertThat(validationFailures, is(empty()));
    verifyNoInteractions(raidRepository);
    verifyNoInteractions(relatedRaidTypeRepository);
  }

  @Test
  void noValidationFailuresRelatedRaidsIsEmpty() {
    final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(Collections.emptyList());

    assertThat(validationFailures, is(empty()));
    verifyNoInteractions(raidRepository);
    verifyNoInteractions(relatedRaidTypeRepository);
  }

  @Test
  void addsValidationFailuresWhenRelatedRaidTypeHasIncorrectUrl() throws URISyntaxException {
    metadataProps.handleUrlPrefix = "handle-url-prefix";
    final var handle = "123.45/67890";
    final var relatedRaidUrl = metadataProps.handleUrlPrefix + "/" + handle;
    final var relatedRaidType = "invalid-url" + "/" + "continues.json";

    when(raidRepository.findByHandle(handle)).thenReturn(Optional.of(mock(RaidRecord.class)));

    final var relatedRaid = new RelatedRaidBlock()
      .relatedRaid(relatedRaidUrl)
      .relatedRaidType(relatedRaidType)
      .relatedRaidTypeSchemeUri(RELATED_RAID_TYPE_SCHEME_URI);

    final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

    final var validationFailure = validationFailures.get(0);
    assertThat(validationFailures.size(), is(1));
    assertThat(validationFailure.getFieldId(), is("relatedRaids[0].relatedRaidType"));
    assertThat(validationFailure.getErrorType(), is("invalid"));
    assertThat(validationFailure.getMessage(), is("RelatedRaidType is invalid."));

    verifyNoInteractions(relatedRaidTypeRepository);
  }

  @Test
  void addsValidationFailuresWhenRelatedRaidNotFound() throws URISyntaxException {
    metadataProps.handleUrlPrefix = "handle-url-prefix";
    final var handle = "123.45/67890";
    final var relatedRaidUrl = metadataProps.handleUrlPrefix + "/" + handle;
    final var relatedRaidType = RELATED_RAID_TYPE_SCHEME_URI + "/" + "continues.json";

    when(raidRepository.findByHandle(handle)).thenReturn(Optional.of(mock(RaidRecord.class)));
    when(relatedRaidTypeRepository.findByUrl(relatedRaidType)).thenReturn(Optional.empty());

    final var relatedRaid = new RelatedRaidBlock()
      .relatedRaid(relatedRaidUrl)
      .relatedRaidType(relatedRaidType)
      .relatedRaidTypeSchemeUri(RELATED_RAID_TYPE_SCHEME_URI);

    final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

    final var validationFailure = validationFailures.get(0);
    assertThat(validationFailures.size(), is(1));
    assertThat(validationFailure.getFieldId(), is("relatedRaids[0].relatedRaidType"));
    assertThat(validationFailure.getErrorType(), is("invalid"));
    assertThat(validationFailure.getMessage(), is("Related Raid Type was not found."));
  }

  @Test
  void addsValidationFailuresWhenRelatedRaidAndTypeNotFound() throws URISyntaxException {
    metadataProps.handleUrlPrefix = "handle-url-prefix";
    final var handle = "123.45/67890";
    final var relatedRaidUrl = metadataProps.handleUrlPrefix + "/" + handle;
    final var relatedRaidType = RELATED_RAID_TYPE_SCHEME_URI + "/" + "continues.json";

    when(raidRepository.findByHandle(handle)).thenReturn(Optional.empty());
    when(relatedRaidTypeRepository.findByUrl(relatedRaidType)).thenReturn(Optional.empty());

    final var relatedRaid = new RelatedRaidBlock()
      .relatedRaid(relatedRaidUrl)
      .relatedRaidType(relatedRaidType)
      .relatedRaidTypeSchemeUri(RELATED_RAID_TYPE_SCHEME_URI);

    final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

    assertThat(validationFailures.size(), is(2));
    assertThat(validationFailures.get(0).getFieldId(), is("relatedRaids[0].relatedRaid"));
    assertThat(validationFailures.get(0).getErrorType(), is("invalid"));
    assertThat(validationFailures.get(0).getMessage(), is("Related Raid was not found."));
    assertThat(validationFailures.get(1).getFieldId(), is("relatedRaids[0].relatedRaidType"));
    assertThat(validationFailures.get(1).getErrorType(), is("invalid"));
    assertThat(validationFailures.get(1).getMessage(), is("Related Raid Type was not found."));
  }


  @Test
  void addsValidationFailureIfRelatedRaidTypeSchemeUriIsNull() {
    metadataProps.handleUrlPrefix = "handle-url-prefix";
    final var handle = "123.45/67890";
    final var relatedRaidUrl = metadataProps.handleUrlPrefix + "/" + handle;
    final var relatedRaidType = RELATED_RAID_TYPE_SCHEME_URI + "/" + "continues.json";

    when(raidRepository.findByHandle(handle)).thenReturn(Optional.of(mock(RaidRecord.class)));
    when(relatedRaidTypeRepository.findByUrl(relatedRaidType)).thenReturn(Optional.of(mock(RelatedRaidTypeRecord.class)));

    final var relatedRaid = new RelatedRaidBlock()
      .relatedRaid(relatedRaidUrl)
      .relatedRaidType(relatedRaidType);

    final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

    assertThat(validationFailures.size(), is(1));
    assertThat(validationFailures.get(0).getFieldId(), is("relatedRaids[0].relatedRaidTypeSchemeUri"));
    assertThat(validationFailures.get(0).getErrorType(), is("required"));
    assertThat(validationFailures.get(0).getMessage(), is("Related Raid Type Scheme URI is required."));
  }

  @Test
  void addsValidationFailureIfRelatedRaidTypeSchemeUriIsInvalid() {
    metadataProps.handleUrlPrefix = "handle-url-prefix";
    final var handle = "123.45/67890";
    final var relatedRaidUrl = metadataProps.handleUrlPrefix + "/" + handle;
    final var relatedRaidType = RELATED_RAID_TYPE_SCHEME_URI + "/" + "continues.json";

    when(raidRepository.findByHandle(handle)).thenReturn(Optional.of(mock(RaidRecord.class)));
    when(relatedRaidTypeRepository.findByUrl(relatedRaidType)).thenReturn(Optional.of(mock(RelatedRaidTypeRecord.class)));

    final var relatedRaid = new RelatedRaidBlock()
      .relatedRaid(relatedRaidUrl)
      .relatedRaidType(relatedRaidType)
      .relatedRaidTypeSchemeUri("invalid");

    final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

    assertThat(validationFailures.size(), is(1));
    assertThat(validationFailures.get(0).getFieldId(), is("relatedRaids[0].relatedRaidTypeSchemeUri"));
    assertThat(validationFailures.get(0).getErrorType(), is("invalid"));
    assertThat(validationFailures.get(0).getMessage(), is("Related Raid Type Scheme URI is invalid."));
  }
}