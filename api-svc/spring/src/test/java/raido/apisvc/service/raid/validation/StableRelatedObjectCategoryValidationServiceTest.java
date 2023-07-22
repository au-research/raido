package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.apisvc.repository.RelatedObjectCategoryRepository;
import raido.apisvc.repository.RelatedObjectCategorySchemeRepository;
import raido.db.jooq.api_svc.tables.records.RelatedObjectCategoryRecord;
import raido.db.jooq.api_svc.tables.records.RelatedObjectCategorySchemeRecord;
import raido.idl.raidv2.model.RelatedObjectCategory;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static raido.apisvc.util.TestConstants.INPUT_RELATED_OBJECT_CATEGORY;
import static raido.apisvc.util.TestConstants.RELATED_OBJECT_CATEGORY_SCHEME_URI;

@ExtendWith(MockitoExtension.class)
class StableRelatedObjectCategoryValidationServiceTest {
  private static final int INDEX = 3;
  private static final int RELATED_OBJECT_CATEGORY_SCHEME_ID = 1;

  private static final RelatedObjectCategorySchemeRecord RELATED_OBJECT_CATEGORY_SCHEME_RECORD =
    new RelatedObjectCategorySchemeRecord()
      .setId(RELATED_OBJECT_CATEGORY_SCHEME_ID)
      .setUri(RELATED_OBJECT_CATEGORY_SCHEME_URI);

  private static final RelatedObjectCategoryRecord RELATED_OBJECT_CATEGORY_RECORD =
    new RelatedObjectCategoryRecord()
      .setSchemeId(RELATED_OBJECT_CATEGORY_SCHEME_ID)
      .setUri(INPUT_RELATED_OBJECT_CATEGORY);

  @Mock
  private RelatedObjectCategoryRepository relatedObjectCategoryRepository;

  @Mock
  private RelatedObjectCategorySchemeRepository relatedObjectCategorySchemeRepository;

  @InjectMocks
  private StableRelatedObjectCategoryValidationService validationService;

  @Test
  @DisplayName("Validation passes with valid related object type")
  void validRelatedObjectCategory() {
    var relatedObjectCategory = new RelatedObjectCategory()
      .id(INPUT_RELATED_OBJECT_CATEGORY)
      .schemeUri(RELATED_OBJECT_CATEGORY_SCHEME_URI);

    when(relatedObjectCategorySchemeRepository.findByUri(RELATED_OBJECT_CATEGORY_SCHEME_URI))
      .thenReturn(Optional.of(RELATED_OBJECT_CATEGORY_SCHEME_RECORD));

    when(relatedObjectCategoryRepository
      .findByUriAndSchemeId(INPUT_RELATED_OBJECT_CATEGORY, RELATED_OBJECT_CATEGORY_SCHEME_ID))
      .thenReturn(Optional.of(RELATED_OBJECT_CATEGORY_RECORD));

    final var failures = validationService.validate(relatedObjectCategory, INDEX);

    assertThat(failures, empty());
  }

  @Test
  @DisplayName("Validation fails with null id")
  void nullId() {
    var relatedObjectCategory = new RelatedObjectCategory()
      .schemeUri(RELATED_OBJECT_CATEGORY_SCHEME_URI);

    when(relatedObjectCategorySchemeRepository.findByUri(RELATED_OBJECT_CATEGORY_SCHEME_URI))
      .thenReturn(Optional.of(RELATED_OBJECT_CATEGORY_SCHEME_RECORD));

    final var failures = validationService.validate(relatedObjectCategory, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("relatedObjects[3].category.id")
        .errorType("notSet")
        .message("field must be set")
    ));

    verifyNoInteractions(relatedObjectCategoryRepository);
  }

  @Test
  @DisplayName("Validation fails with empty id")
  void emptyId() {
    var relatedObjectCategory = new RelatedObjectCategory()
      .id("")
      .schemeUri(RELATED_OBJECT_CATEGORY_SCHEME_URI);

    when(relatedObjectCategorySchemeRepository.findByUri(RELATED_OBJECT_CATEGORY_SCHEME_URI))
      .thenReturn(Optional.of(RELATED_OBJECT_CATEGORY_SCHEME_RECORD));

    final var failures = validationService.validate(relatedObjectCategory, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("relatedObjects[3].category.id")
        .errorType("notSet")
        .message("field must be set")
    ));

    verifyNoInteractions(relatedObjectCategoryRepository);
  }

  @Test
  @DisplayName("Validation fails with null schemeUri")
  void nullSchemeUri() {
    var relatedObjectCategory = new RelatedObjectCategory()
      .id(INPUT_RELATED_OBJECT_CATEGORY);

    final var failures = validationService.validate(relatedObjectCategory, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("relatedObjects[3].category.schemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));
    verifyNoInteractions(relatedObjectCategorySchemeRepository);
    verifyNoInteractions(relatedObjectCategoryRepository);
  }

  @Test
  @DisplayName("Validation fails with empty schemeUri")
  void emptySchemeUri() {
    var relatedObjectCategory = new RelatedObjectCategory()
      .id(INPUT_RELATED_OBJECT_CATEGORY)
      .schemeUri("");

    final var failures = validationService.validate(relatedObjectCategory, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("relatedObjects[3].category.schemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));
    verifyNoInteractions(relatedObjectCategorySchemeRepository);
    verifyNoInteractions(relatedObjectCategoryRepository);
  }

  @Test
  @DisplayName("Validation fails if schemeUri does not exist")
  void nonExistentSchemeUri() {
    var relatedObjectCategory = new RelatedObjectCategory()
      .id(INPUT_RELATED_OBJECT_CATEGORY)
      .schemeUri(RELATED_OBJECT_CATEGORY_SCHEME_URI);

    when(relatedObjectCategorySchemeRepository.findByUri(RELATED_OBJECT_CATEGORY_SCHEME_URI))
      .thenReturn(Optional.empty());

    final var failures = validationService.validate(relatedObjectCategory, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("relatedObjects[3].category.schemeUri")
        .errorType("invalidValue")
        .message("scheme is unknown/unsupported")
    ));
  }

  @Test
  @DisplayName("Validation fails if type does not exist with scheme")
  void invalidTypeForScheme() {
    var relatedObjectCategory = new RelatedObjectCategory()
      .id(INPUT_RELATED_OBJECT_CATEGORY)
      .schemeUri(RELATED_OBJECT_CATEGORY_SCHEME_URI);

    when(relatedObjectCategorySchemeRepository.findByUri(RELATED_OBJECT_CATEGORY_SCHEME_URI))
      .thenReturn(Optional.of(RELATED_OBJECT_CATEGORY_SCHEME_RECORD));

    when(relatedObjectCategoryRepository
      .findByUriAndSchemeId(INPUT_RELATED_OBJECT_CATEGORY, RELATED_OBJECT_CATEGORY_SCHEME_ID))
      .thenReturn(Optional.empty());

    final var failures = validationService.validate(relatedObjectCategory, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("relatedObjects[3].category.id")
        .errorType("invalidValue")
        .message("id does not exist within the given scheme")
    ));
  }
}