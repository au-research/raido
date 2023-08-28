package au.org.raid.api.validator;

import au.org.raid.api.repository.AccessTypeRepository;
import au.org.raid.api.repository.AccessTypeSchemaRepository;
import au.org.raid.api.util.TestConstants;
import au.org.raid.db.jooq.api_svc.tables.records.AccessTypeRecord;
import au.org.raid.db.jooq.api_svc.tables.records.AccessTypeSchemeRecord;
import au.org.raid.idl.raidv2.model.AccessTypeWithSchemaUri;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessTypeValidatorTest {
    private static final int ACCESS_TYPE_SCHEMA_ID = 1;

    private static final AccessTypeSchemeRecord ACCESS_TYPE_SCHEMA_RECORD = new AccessTypeSchemeRecord()
            .setId(ACCESS_TYPE_SCHEMA_ID)
            .setUri(TestConstants.ACCESS_TYPE_SCHEMA_URI);

    private static final AccessTypeRecord ACCESS_TYPE_RECORD = new AccessTypeRecord()
            .setSchemeId(ACCESS_TYPE_SCHEMA_ID)
            .setUri(TestConstants.OPEN_ACCESS_TYPE_ID);

    @Mock
    private AccessTypeSchemaRepository accessTypeSchemaRepository;
    @Mock
    private AccessTypeRepository accessTypeRepository;
    @InjectMocks
    private AccessTypeValidator validationService;


    @Test
    @DisplayName("Validation passes with valid access type")
    void validAccessType() {
        final var accessType = new AccessTypeWithSchemaUri()
                .id(TestConstants.OPEN_ACCESS_TYPE_ID)
                .schemaUri(TestConstants.ACCESS_TYPE_SCHEMA_URI);

        when(accessTypeSchemaRepository.findByUri(TestConstants.ACCESS_TYPE_SCHEMA_URI))
                .thenReturn(Optional.of(ACCESS_TYPE_SCHEMA_RECORD));
        when(accessTypeRepository.findByUriAndSchemeId(TestConstants.OPEN_ACCESS_TYPE_ID, ACCESS_TYPE_SCHEMA_ID))
                .thenReturn(Optional.of(ACCESS_TYPE_RECORD));

        final var failures = validationService.validate(accessType);

        assertThat(failures, empty());

        verify(accessTypeSchemaRepository).findByUri(TestConstants.ACCESS_TYPE_SCHEMA_URI);
        verify(accessTypeRepository).findByUriAndSchemeId(TestConstants.OPEN_ACCESS_TYPE_ID, ACCESS_TYPE_SCHEMA_ID);
    }

    @Test
    @DisplayName("Validation fails when id is null")
    void nullId() {
        final var accessType = new AccessTypeWithSchemaUri()
                .schemaUri(TestConstants.ACCESS_TYPE_SCHEMA_URI);

        when(accessTypeSchemaRepository.findByUri(TestConstants.ACCESS_TYPE_SCHEMA_URI))
                .thenReturn(Optional.of(ACCESS_TYPE_SCHEMA_RECORD));

        final var failures = validationService.validate(accessType);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("access.type.id")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }

    @Test
    @DisplayName("Validation fails when id is empty string")
    void emptyId() {
        final var accessType = new AccessTypeWithSchemaUri()
                .id("")
                .schemaUri(TestConstants.ACCESS_TYPE_SCHEMA_URI);

        when(accessTypeSchemaRepository.findByUri(TestConstants.ACCESS_TYPE_SCHEMA_URI))
                .thenReturn(Optional.of(ACCESS_TYPE_SCHEMA_RECORD));

        final var failures = validationService.validate(accessType);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("access.type.id")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }

    @Test
    @DisplayName("Validation fails when schemaUri is null")
    void nullSchemeUri() {
        final var accessType = new AccessTypeWithSchemaUri()
                .id(TestConstants.OPEN_ACCESS_TYPE_ID);

        final var failures = validationService.validate(accessType);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("access.type.schemaUri")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }

    @Test
    @DisplayName("Validation fails when schemaUri is empty")
    void emptySchemeUri() {
        final var accessType = new AccessTypeWithSchemaUri()
                .id(TestConstants.OPEN_ACCESS_TYPE_ID)
                .schemaUri("");

        final var failures = validationService.validate(accessType);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("access.type.schemaUri")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }

    @Test
    @DisplayName("Validation fails when schemaUri is invalid")
    void invalidSchemeUri() {
        final var accessType = new AccessTypeWithSchemaUri()
                .id(TestConstants.OPEN_ACCESS_TYPE_ID)
                .schemaUri(TestConstants.ACCESS_TYPE_SCHEMA_URI);

        when(accessTypeSchemaRepository.findByUri(TestConstants.ACCESS_TYPE_SCHEMA_URI))
                .thenReturn(Optional.empty());

        final var failures = validationService.validate(accessType);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("access.type.schemaUri")
                        .errorType("invalidValue")
                        .message("schema is unknown/unsupported")
        ));
    }

    @Test
    @DisplayName("Validation fails when type is null")
    void nullType() {
        final var failures = validationService.validate(null);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("access.type")
                        .errorType("notSet")
                        .message("field must be set")
        ));

        verifyNoInteractions(accessTypeSchemaRepository);
        verifyNoInteractions(accessTypeRepository);
    }

    @Test
    @DisplayName("Validation fails when id not found in schema")
    void invalidTypeForScheme() {
        final var accessType = new AccessTypeWithSchemaUri()
                .id(TestConstants.OPEN_ACCESS_TYPE_ID)
                .schemaUri(TestConstants.ACCESS_TYPE_SCHEMA_URI);

        when(accessTypeSchemaRepository.findByUri(TestConstants.ACCESS_TYPE_SCHEMA_URI))
                .thenReturn(Optional.of(ACCESS_TYPE_SCHEMA_RECORD));

        when(accessTypeRepository.findByUriAndSchemeId(TestConstants.OPEN_ACCESS_TYPE_ID, ACCESS_TYPE_SCHEMA_ID))
                .thenReturn(Optional.empty());

        final var failures = validationService.validate(accessType);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("access.type.id")
                        .errorType("invalidValue")
                        .message("id does not exist within the given schema")
        ));
    }
}