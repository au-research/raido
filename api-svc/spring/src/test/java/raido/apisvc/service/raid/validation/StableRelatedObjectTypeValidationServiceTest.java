package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.apisvc.repository.RelatedObjectTypeRepository;
import raido.apisvc.repository.RelatedObjectTypeSchemeRepository;
import raido.db.jooq.api_svc.tables.records.RelatedObjectTypeRecord;
import raido.db.jooq.api_svc.tables.records.RelatedObjectTypeSchemeRecord;
import raido.idl.raidv2.model.RelatedObjectType;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static raido.apisvc.util.TestConstants.BOOK_CHAPTER_RELATED_OBJECT_TYPE;
import static raido.apisvc.util.TestConstants.RELATED_OBJECT_TYPE_SCHEME_URI;

@ExtendWith(MockitoExtension.class)
class StableRelatedObjectTypeValidationServiceTest {
  private static final int INDEX = 3;
  private static final int RELATED_OBJECT_TYPE_SCHEME_ID = 1;

  private static final RelatedObjectTypeSchemeRecord RELATED_OBJECT_TYPE_SCHEME_RECORD =
    new RelatedObjectTypeSchemeRecord()
      .setId(RELATED_OBJECT_TYPE_SCHEME_ID)
      .setUri(RELATED_OBJECT_TYPE_SCHEME_URI);

  private static final RelatedObjectTypeRecord RELATED_OBJECT_TYPE_RECORD =
    new RelatedObjectTypeRecord()
      .setSchemeId(RELATED_OBJECT_TYPE_SCHEME_ID)
      .setUri(BOOK_CHAPTER_RELATED_OBJECT_TYPE);

  @Mock
  private RelatedObjectTypeRepository relatedObjectTypeRepository;

  @Mock
  private RelatedObjectTypeSchemeRepository relatedObjectTypeSchemeRepository;

  @InjectMocks
  private StableRelatedObjectTypeValidationService validationService;

  @Test
  @DisplayName("Validation passes with valid related object type")
  void validRelatedObjectType() {
    var relatedObjectType = new RelatedObjectType()
      .id(BOOK_CHAPTER_RELATED_OBJECT_TYPE)
      .schemeUri(RELATED_OBJECT_TYPE_SCHEME_URI);

    when(relatedObjectTypeSchemeRepository.findByUri(RELATED_OBJECT_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(RELATED_OBJECT_TYPE_SCHEME_RECORD));

    when(relatedObjectTypeRepository
      .findByUriAndSchemeId(BOOK_CHAPTER_RELATED_OBJECT_TYPE, RELATED_OBJECT_TYPE_SCHEME_ID))
      .thenReturn(Optional.of(RELATED_OBJECT_TYPE_RECORD));

    final var failures = validationService.validate(relatedObjectType, INDEX);

    assertThat(failures, empty());
  }

  @Test
  @DisplayName("Validation fails with null id")
  void nullId() {
    var relatedObjectType = new RelatedObjectType()
      .schemeUri(RELATED_OBJECT_TYPE_SCHEME_URI);

    when(relatedObjectTypeSchemeRepository.findByUri(RELATED_OBJECT_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(RELATED_OBJECT_TYPE_SCHEME_RECORD));

    final var failures = validationService.validate(relatedObjectType, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("relatedObjects[3].type.id")
        .errorType("notSet")
        .message("field must be set")
    ));

    verifyNoInteractions(relatedObjectTypeRepository);
  }

  @Test
  @DisplayName("Validation fails with empty id")
  void emptyId() {
    var relatedObjectType = new RelatedObjectType()
      .id("")
      .schemeUri(RELATED_OBJECT_TYPE_SCHEME_URI);

    when(relatedObjectTypeSchemeRepository.findByUri(RELATED_OBJECT_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(RELATED_OBJECT_TYPE_SCHEME_RECORD));

    final var failures = validationService.validate(relatedObjectType, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("relatedObjects[3].type.id")
        .errorType("notSet")
        .message("field must be set")
    ));
    verifyNoInteractions(relatedObjectTypeRepository);
  }

  @Test
  @DisplayName("Validation fails with null schemeUri")
  void nullSchemeUri() {
    var relatedObjectType = new RelatedObjectType()
      .id(BOOK_CHAPTER_RELATED_OBJECT_TYPE);

    final var failures = validationService.validate(relatedObjectType, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("relatedObjects[3].type.schemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));
    verifyNoInteractions(relatedObjectTypeSchemeRepository);
    verifyNoInteractions(relatedObjectTypeRepository);
  }

  @Test
  @DisplayName("Validation fails with empty schemeUri")
  void emptySchemeUri() {
    var relatedObjectType = new RelatedObjectType()
      .id(BOOK_CHAPTER_RELATED_OBJECT_TYPE)
      .schemeUri("");

    final var failures = validationService.validate(relatedObjectType, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("relatedObjects[3].type.schemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));
    verifyNoInteractions(relatedObjectTypeSchemeRepository);
    verifyNoInteractions(relatedObjectTypeRepository);
  }

  @Test
  @DisplayName("Validation fails if schemeUri does not exist")
  void nonExistentSchemeUri() {
    var relatedObjectType = new RelatedObjectType()
      .id(BOOK_CHAPTER_RELATED_OBJECT_TYPE)
      .schemeUri(RELATED_OBJECT_TYPE_SCHEME_URI);

    when(relatedObjectTypeSchemeRepository.findByUri(RELATED_OBJECT_TYPE_SCHEME_URI))
      .thenReturn(Optional.empty());

    final var failures = validationService.validate(relatedObjectType, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("relatedObjects[3].type.schemeUri")
        .errorType("invalidValue")
        .message("scheme is unknown/unsupported")
    ));
  }

  @Test
  @DisplayName("Validation fails if type does not exist with scheme")
  void invalidTypeForScheme() {
    var relatedObjectType = new RelatedObjectType()
      .id(BOOK_CHAPTER_RELATED_OBJECT_TYPE)
      .schemeUri(RELATED_OBJECT_TYPE_SCHEME_URI);

    when(relatedObjectTypeSchemeRepository.findByUri(RELATED_OBJECT_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(RELATED_OBJECT_TYPE_SCHEME_RECORD));

    when(relatedObjectTypeRepository
      .findByUriAndSchemeId(BOOK_CHAPTER_RELATED_OBJECT_TYPE, RELATED_OBJECT_TYPE_SCHEME_ID))
      .thenReturn(Optional.empty());

    final var failures = validationService.validate(relatedObjectType, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("relatedObjects[3].type.id")
        .errorType("invalidValue")
        .message("id does not exist within the given scheme")
    ));
  }
}