package au.org.raid.api.service.raid.validation;

import au.org.raid.api.repository.RelatedObjectTypeRepository;
import au.org.raid.api.service.doi.DoiService;
import au.org.raid.db.jooq.api_svc.tables.records.RelatedObjectTypeRecord;
import au.org.raid.idl.raidv2.model.RelatedObjectBlock;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RelatedObjectValidationServiceTest {
    @Mock
    private RelatedObjectTypeRepository relatedObjectTypeRepository;

    @Mock
    private DoiService doiService;

    @InjectMocks
    private RelatedObjectValidationService validationService;

    @Test
    void noFailuresWithValidRelatedObject() {
        final var relatedObjectType =
                "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/book-chapter.json";

        final var relatedObject = new RelatedObjectBlock()
                .relatedObject("https://doi.org/10.000/00000")
                .relatedObjectSchemeUri("https://doi.org/")
                .relatedObjectType(relatedObjectType)
                .relatedObjectTypeSchemeUri("https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/type/v1/")
                .relatedObjectCategory("Input");

        when(relatedObjectTypeRepository.findByUriAndSchemaId(relatedObjectType, 1))
                .thenReturn(Optional.of(new RelatedObjectTypeRecord()));

        final var failures =
                validationService.validateRelatedObjects(Collections.singletonList(relatedObject));

        assertThat(failures, is(empty()));
    }

    @Test
    void noFailuresIfListIsEmpty() {
        final var failures = validationService.validateRelatedObjects(Collections.emptyList());

        assertThat(failures, is(empty()));
    }

    @Test
    void noFailuresIfListIsNull() {
        final var failures = validationService.validateRelatedObjects(null);

        assertThat(failures, is(empty()));
    }

    @Test
    void addsFailuresIfRelatedObjectIsNull() {
        final var relatedObjectType =
                "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/book-chapter.json";

        final var relatedObject = new RelatedObjectBlock()
                .relatedObjectSchemeUri("https://doi.org/")
                .relatedObjectType(relatedObjectType)
                .relatedObjectTypeSchemeUri("https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/type/v1/")
                .relatedObjectCategory("Input");

        when(relatedObjectTypeRepository.findByUriAndSchemaId(relatedObjectType, 1))
                .thenReturn(Optional.of(new RelatedObjectTypeRecord()));

        final var failures =
                validationService.validateRelatedObjects(Collections.singletonList(relatedObject));

        var failure = failures.get(0);
        assertThat(failures.size(), is(1));

        assertThat(failure.getFieldId(), is("relatedObjects[0].relatedObject"));
        assertThat(failure.getErrorType(), is("required"));
        assertThat(failure.getMessage(), is("This is a required field."));
    }

    @Test
    void addsFailureIfRelatedObjectSchemeUriIsNull() {
        final var relatedObjectType =
                "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/book-chapter.json";

        final var relatedObject = new RelatedObjectBlock()
                .relatedObject("https://doi.org/10.000/00000")
                .relatedObjectType(relatedObjectType)
                .relatedObjectTypeSchemeUri("https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/type/v1/")
                .relatedObjectCategory("Input");

        when(relatedObjectTypeRepository.findByUriAndSchemaId(relatedObjectType, 1))
                .thenReturn(Optional.of(new RelatedObjectTypeRecord()));

        final var failures =
                validationService.validateRelatedObjects(Collections.singletonList(relatedObject));

        var failure = failures.get(0);
        assertThat(failures.size(), is(1));

        assertThat(failure.getFieldId(), is("relatedObjects[0].relatedObjectSchemeUri"));
        assertThat(failure.getErrorType(), is("required"));
        assertThat(failure.getMessage(), is("This is a required field."));
    }

    @Test
    void addsFailureIfRelatedObjectTypeIsNull() {
        final var relatedObject = new RelatedObjectBlock()
                .relatedObject("https://doi.org/10.000/00000")
                .relatedObjectSchemeUri("https://doi.org/")
                .relatedObjectTypeSchemeUri("https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/type/v1/")
                .relatedObjectCategory("Input");

        final var failures =
                validationService.validateRelatedObjects(Collections.singletonList(relatedObject));

        var failure = failures.get(0);
        assertThat(failures.size(), is(1));

        assertThat(failure.getFieldId(), is("relatedObjects[0].relatedObjectType"));
        assertThat(failure.getErrorType(), is("required"));
        assertThat(failure.getMessage(), is("This is a required field."));
    }

    @Test
    void addsFailureIfRelatedObjectTypeSchemeUriIsNull() {
        final var relatedObjectType =
                "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/book-chapter.json";

        final var relatedObject = new RelatedObjectBlock()
                .relatedObject("https://doi.org/10.000/00000")
                .relatedObjectSchemeUri("https://doi.org/")
                .relatedObjectType(relatedObjectType)
                .relatedObjectCategory("Input");

        when(relatedObjectTypeRepository.findByUriAndSchemaId(relatedObjectType, 1))
                .thenReturn(Optional.of(new RelatedObjectTypeRecord()));

        final var failures =
                validationService.validateRelatedObjects(Collections.singletonList(relatedObject));

        var failure = failures.get(0);
        assertThat(failures.size(), is(1));

        assertThat(failure.getFieldId(), is("relatedObjects[0].relatedObjectTypeSchemeUri"));
        assertThat(failure.getErrorType(), is("required"));
        assertThat(failure.getMessage(), is("This is a required field."));
    }

    @Test
    void addsFailureIfRelatedObjectCategoryIsNull() {
        final var relatedObjectType =
                "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/book-chapter.json";

        final var relatedObject = new RelatedObjectBlock()
                .relatedObject("https://doi.org/10.000/00000")
                .relatedObjectSchemeUri("https://doi.org/")
                .relatedObjectType(relatedObjectType)
                .relatedObjectTypeSchemeUri("https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/type/v1/");

        when(relatedObjectTypeRepository.findByUriAndSchemaId(relatedObjectType, 1))
                .thenReturn(Optional.of(new RelatedObjectTypeRecord()));

        final var failures =
                validationService.validateRelatedObjects(Collections.singletonList(relatedObject));

        var failure = failures.get(0);
        assertThat(failures.size(), is(1));

        assertThat(failure.getFieldId(), is("relatedObjects[0].relatedObjectCategory"));
        assertThat(failure.getErrorType(), is("required"));
        assertThat(failure.getMessage(), is("This is a required field."));
    }

    @Test
    void addsFailuresIfRelatedObjectSchemeUriIsIncorrect() {
        final var relatedObjectType =
                "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/book-chapter.json";

        final var relatedObject = new RelatedObjectBlock()
                .relatedObject("https://doi.org/10.000/00000")
                .relatedObjectSchemeUri("https://blah.org/")
                .relatedObjectType(relatedObjectType)
                .relatedObjectTypeSchemeUri("https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/type/v1/")
                .relatedObjectCategory("Input");

        when(relatedObjectTypeRepository.findByUriAndSchemaId(relatedObjectType, 1))
                .thenReturn(Optional.of(new RelatedObjectTypeRecord()));

        final var failures =
                validationService.validateRelatedObjects(Collections.singletonList(relatedObject));

        var failure = failures.get(0);
        assertThat(failures.size(), is(1));

        assertThat(failure.getFieldId(), is("relatedObjects[0].relatedObjectSchemeUri"));
        assertThat(failure.getErrorType(), is("invalid"));
        assertThat(failure.getMessage(), is("Only https://doi.org/ is supported."));
    }

    @Test
    void addsFailureIfRelatedObjectTypeSchemeUriIsIncorrect() {
        final var relatedObjectType =
                "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/book-chapter.json";

        final var relatedObject = new RelatedObjectBlock()
                .relatedObject("https://doi.org/10.000/00000")
                .relatedObjectSchemeUri("https://doi.org/")
                .relatedObjectType(relatedObjectType)
                .relatedObjectTypeSchemeUri("https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/")
                .relatedObjectCategory("Input");

        when(relatedObjectTypeRepository.findByUriAndSchemaId(relatedObjectType, 1))
                .thenReturn(Optional.of(new RelatedObjectTypeRecord()));

        final var failures =
                validationService.validateRelatedObjects(Collections.singletonList(relatedObject));

        var failure = failures.get(0);
        assertThat(failures.size(), is(1));

        assertThat(failure.getFieldId(), is("relatedObjects[0].relatedObjectTypeSchemeUri"));
        assertThat(failure.getErrorType(), is("invalid"));
        assertThat(failure.getMessage(), is("Only https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/type/v1/ is supported."));
    }

    @Test
    void addsFailureIfRelatedObjectTypeIsNotFound() {
        final var relatedObjectType =
                "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/book-chapter.json";

        final var relatedObject = new RelatedObjectBlock()
                .relatedObject("https://doi.org/10.000/00000")
                .relatedObjectSchemeUri("https://doi.org/")
                .relatedObjectType(relatedObjectType)
                .relatedObjectTypeSchemeUri("https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/type/v1/")
                .relatedObjectCategory("Input");

        when(relatedObjectTypeRepository.findByUriAndSchemaId(relatedObjectType, 1))
                .thenReturn(Optional.empty());

        final var failures =
                validationService.validateRelatedObjects(Collections.singletonList(relatedObject));

        var failure = failures.get(0);
        assertThat(failures.size(), is(1));

        assertThat(failure.getFieldId(), is("relatedObjects[0].relatedObjectType"));
        assertThat(failure.getErrorType(), is("invalid"));
        assertThat(failure.getMessage(), is("Related object type was not found."));
    }

    @Test
    void addsFailureIfRelatedObjectTypeHasInvalidUrl() {
        final var relatedObjectType =
                "https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/type/v1/book-chapter.json";

        final var relatedObject = new RelatedObjectBlock()
                .relatedObject("https://doi.org/10.000/00000")
                .relatedObjectSchemeUri("https://doi.org/")
                .relatedObjectType(relatedObjectType)
                .relatedObjectTypeSchemeUri("https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/type/v1/")
                .relatedObjectCategory("Input");

        final var failures =
                validationService.validateRelatedObjects(Collections.singletonList(relatedObject));

        var failure = failures.get(0);
        assertThat(failures.size(), is(1));

        assertThat(failure.getFieldId(), is("relatedObjects[0].relatedObjectType"));
        assertThat(failure.getErrorType(), is("invalid"));
        assertThat(failure.getMessage(), is("Related object type is invalid."));

        verifyNoInteractions(relatedObjectTypeRepository);
    }

    @Test
    void addsFailureIfRelatedObjectCategoryIsInvalid() {
        final var relatedObjectType =
                "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/book-chapter.json";

        final var relatedObject = new RelatedObjectBlock()
                .relatedObject("https://doi.org/10.000/00000")
                .relatedObjectSchemeUri("https://doi.org/")
                .relatedObjectType(relatedObjectType)
                .relatedObjectTypeSchemeUri("https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/type/v1/")
                .relatedObjectCategory("No a category");

        when(relatedObjectTypeRepository.findByUriAndSchemaId(relatedObjectType, 1))
                .thenReturn(Optional.of(new RelatedObjectTypeRecord()));

        final var failures =
                validationService.validateRelatedObjects(Collections.singletonList(relatedObject));

        var failure = failures.get(0);
        assertThat(failures.size(), is(1));

        assertThat(failure.getFieldId(), is("relatedObjects[0].relatedObjectCategory"));
        assertThat(failure.getErrorType(), is("invalid"));
        assertThat(failure.getMessage(), is("Should be one of 'Input', 'Output', 'Internal process document or artefact'."));
    }


    @Test
    void addsFailureIfDoiDoesNotExist() {
        final var fieldId = "relatedObjects[0].relatedObject";
        final var doi = "https://doi.org/10.000/00000";
        final var relatedObjectType =
                "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/book-chapter.json";
        final var errorMessage = "doi does not exist";

        final var relatedObject = new RelatedObjectBlock()
                .relatedObject(doi)
                .relatedObjectSchemeUri("https://doi.org/")
                .relatedObjectType(relatedObjectType)
                .relatedObjectTypeSchemeUri("https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/type/v1/")
                .relatedObjectCategory("No a category")
                .relatedObjectCategory("Input");

        when(relatedObjectTypeRepository.findByUriAndSchemaId(relatedObjectType, 1))
                .thenReturn(Optional.of(new RelatedObjectTypeRecord()));

        final var failure = new ValidationFailure()
                .fieldId(fieldId)
                .errorType("invalidValue")
                .message("uri not found");

        when(doiService.validate(doi, fieldId))
                .thenReturn(List.of(failure));

        final var failures =
                validationService.validateRelatedObjects(List.of(relatedObject));

        assertThat(failures, is(List.of(failure)));
    }
}