package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.apisvc.service.doi.DoiService;
import raido.idl.raidv2.model.RelatedObject;
import raido.idl.raidv2.model.RelatedObjectCategory;
import raido.idl.raidv2.model.RelatedObjectType;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static raido.apisvc.endpoint.message.ValidationMessage.FIELD_MUST_BE_SET_MESSAGE;
import static raido.apisvc.endpoint.message.ValidationMessage.NOT_SET_TYPE;
import static raido.apisvc.util.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class StableRelatedObjectValidationServiceTest {
  @Mock
  private StableRelatedObjectTypeValidationService typeValidationService;

  @Mock
  private StableRelatedObjectCategoryValidationService categoryValidationService;

  @Mock
  private DoiService doiService;

  @InjectMocks
  private StableRelatedObjectValidationService validationService;


  // identifierSchemeUri is empty

  // identifierSchemeUri is invalid

  // type and category errors are returned

  @Test
  @DisplayName("Validation passes with valid related object")
  void validaRelatedObject() {
    final var type = new RelatedObjectType()
      .id(BOOK_CHAPTER_RELATED_OBJECT_TYPE)
      .schemeUri(RELATED_OBJECT_TYPE_SCHEME_URI);

    final var category = new RelatedObjectCategory()
      .id(INPUT_RELATED_OBJECT_CATEGORY)
      .schemeUri(RELATED_OBJECT_CATEGORY_SCHEME_URI);

    final var relatedObject = new RelatedObject()
      .id(VALID_DOI)
      .identifierSchemeUri(HTTPS_DOI_ORG)
      .type(type)
      .category(category);

    when(typeValidationService.validate(type, 0)).thenReturn(Collections.emptyList());
    when(categoryValidationService.validate(category, 0)).thenReturn(Collections.emptyList());

    final var failures =
      validationService.validateRelatedObjects(Collections.singletonList(relatedObject));

    assertThat(failures, empty());
  }

  @Test
  @DisplayName("Passes validation with empty related objects")
  void emptyRelatedObjects() {
    final var failures = validationService.validateRelatedObjects(Collections.emptyList());

    assertThat(failures, empty());
  }

  @Test
  @DisplayName("Passes validation with null related objects")
  void nullRelatedObjects() {
    final var failures = validationService.validateRelatedObjects(null);

    assertThat(failures, empty());
  }

  @Test
  @DisplayName("Fails validation with null related object id")
  void nullId() {
    final var type = new RelatedObjectType()
      .id(BOOK_CHAPTER_RELATED_OBJECT_TYPE)
      .schemeUri(RELATED_OBJECT_TYPE_SCHEME_URI);

    final var category = new RelatedObjectCategory()
      .id(INPUT_RELATED_OBJECT_CATEGORY)
      .schemeUri(RELATED_OBJECT_CATEGORY_SCHEME_URI);

    final var relatedObject = new RelatedObject()
      .identifierSchemeUri(HTTPS_DOI_ORG)
      .type(type)
      .category(category);

    final var failures =
      validationService.validateRelatedObjects(Collections.singletonList(relatedObject));

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
      .fieldId("relatedObjects[0].id")
      .errorType("notSet")
      .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Fails validation with empty related object id")
  void emptyId() {
    final var type = new RelatedObjectType()
      .id(BOOK_CHAPTER_RELATED_OBJECT_TYPE)
      .schemeUri(RELATED_OBJECT_TYPE_SCHEME_URI);

    final var category = new RelatedObjectCategory()
      .id(INPUT_RELATED_OBJECT_CATEGORY)
      .schemeUri(RELATED_OBJECT_CATEGORY_SCHEME_URI);

    final var relatedObject = new RelatedObject()
      .id("")
      .identifierSchemeUri(HTTPS_DOI_ORG)
      .type(type)
      .category(category);

    final var failures =
      validationService.validateRelatedObjects(Collections.singletonList(relatedObject));

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("relatedObjects[0].id")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Fails validation with null identifierSchemeUri")
  void nullSchemeUri() {
    final var type = new RelatedObjectType()
      .id(BOOK_CHAPTER_RELATED_OBJECT_TYPE)
      .schemeUri(RELATED_OBJECT_TYPE_SCHEME_URI);

    final var category = new RelatedObjectCategory()
      .id(INPUT_RELATED_OBJECT_CATEGORY)
      .schemeUri(RELATED_OBJECT_CATEGORY_SCHEME_URI);

    final var relatedObject = new RelatedObject()
      .id(VALID_DOI)
      .type(type)
      .category(category);

    final var failures =
      validationService.validateRelatedObjects(Collections.singletonList(relatedObject));

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("relatedObjects[0].identifierSchemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Fails validation with empty identifierSchemeUri")
  void emptySchemeUri() {
    final var type = new RelatedObjectType()
      .id(BOOK_CHAPTER_RELATED_OBJECT_TYPE)
      .schemeUri(RELATED_OBJECT_TYPE_SCHEME_URI);

    final var category = new RelatedObjectCategory()
      .id(INPUT_RELATED_OBJECT_CATEGORY)
      .schemeUri(RELATED_OBJECT_CATEGORY_SCHEME_URI);

    final var relatedObject = new RelatedObject()
      .id(VALID_DOI)
      .identifierSchemeUri("")
      .type(type)
      .category(category);

    final var failures =
      validationService.validateRelatedObjects(Collections.singletonList(relatedObject));

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("relatedObjects[0].identifierSchemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Validation fails if DOI does not exist")
  void addsFailureIfDoiDoesNotExist() {
    final var errorMessage = "doi does not exist";
    final var type = new RelatedObjectType()
      .id(BOOK_CHAPTER_RELATED_OBJECT_TYPE)
      .schemeUri(RELATED_OBJECT_TYPE_SCHEME_URI);

    final var category = new RelatedObjectCategory()
      .id(INPUT_RELATED_OBJECT_CATEGORY)
      .schemeUri(RELATED_OBJECT_CATEGORY_SCHEME_URI);

    final var relatedObject = new RelatedObject()
      .id(VALID_DOI)
      .identifierSchemeUri(HTTPS_DOI_ORG)
      .type(type)
      .category(category);

    when(typeValidationService.validate(type, 0)).thenReturn(Collections.emptyList());
    when(categoryValidationService.validate(category, 0)).thenReturn(Collections.emptyList());
    when(doiService.validateDoiExists(VALID_DOI)).thenReturn(Collections.singletonList(errorMessage));

    final var failures =
      validationService.validateRelatedObjects(Collections.singletonList(relatedObject));

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("relatedObjects[0].id")
        .errorType("invalidValue")
        .message(errorMessage)
    ));
  }

  @Test
  @DisplayName("Validation failures in type and category are returned")
  void typeAndCategoryFailuresAreReturned() {
    final var type = new RelatedObjectType()
      .id(BOOK_CHAPTER_RELATED_OBJECT_TYPE)
      .schemeUri(RELATED_OBJECT_TYPE_SCHEME_URI);

    final var category = new RelatedObjectCategory()
      .id(INPUT_RELATED_OBJECT_CATEGORY)
      .schemeUri(RELATED_OBJECT_CATEGORY_SCHEME_URI);

    final var relatedObject = new RelatedObject()
      .id(VALID_DOI)
      .identifierSchemeUri(HTTPS_DOI_ORG)
      .type(type)
      .category(category);

    final var typeError = new ValidationFailure()
      .fieldId("relatedObjects[0].type.id")
      .errorType(NOT_SET_TYPE)
      .message(FIELD_MUST_BE_SET_MESSAGE);

    final var categoryError = new ValidationFailure()
      .fieldId("relatedObjects[0].category.id")
      .errorType(NOT_SET_TYPE)
      .message(FIELD_MUST_BE_SET_MESSAGE);

    when(typeValidationService.validate(type, 0)).thenReturn(List.of(typeError));
    when(categoryValidationService.validate(category, 0)).thenReturn(List.of(categoryError));

    final var failures =
      validationService.validateRelatedObjects(Collections.singletonList(relatedObject));

    assertThat(failures, hasSize(2));
    assertThat(failures, hasItems(typeError, categoryError));
  }
}