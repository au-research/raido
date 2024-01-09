package au.org.raid.api.service;

import au.org.raid.api.exception.RelatedObjectTypeNotFoundException;
import au.org.raid.api.exception.RelatedObjectTypeSchemaNotFoundException;
import au.org.raid.api.factory.RelatedObjectTypeFactory;
import au.org.raid.api.repository.RelatedObjectTypeRepository;
import au.org.raid.api.repository.RelatedObjectTypeSchemaRepository;
import au.org.raid.db.jooq.tables.records.RelatedObjectTypeRecord;
import au.org.raid.db.jooq.tables.records.RelatedObjectTypeSchemaRecord;
import au.org.raid.idl.raidv2.model.RelatedObjectType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RelatedObjectTypeServiceTest {
    @Mock
    private RelatedObjectTypeRepository relatedObjectTypeRepository;
    @Mock
    private RelatedObjectTypeSchemaRepository relatedObjectTypeSchemaRepository;
    @Mock
    private RelatedObjectTypeFactory relatedObjectTypeFactory;
    @InjectMocks
    private RelatedObjectTypeService relatedObjectTypeService;
    @Test
    @DisplayName("findById() returns related object type")
    void findById() {
        final var id = 123;
        final var schemaId = 234;
        final var uri = "_uri";
        final var schemaUri = "schema-uri";

        final var relatedObjectTypeRecord = new RelatedObjectTypeRecord()
                .setSchemaId(schemaId)
                .setUri(uri);

        final var relatedObjectTypeSchemaRecord = new RelatedObjectTypeSchemaRecord()
                .setUri(schemaUri);

        final var relatedObjectType = new RelatedObjectType();

        when(relatedObjectTypeRepository.findById(id)).thenReturn(Optional.of(relatedObjectTypeRecord));
        when(relatedObjectTypeSchemaRepository.findById(schemaId)).thenReturn(Optional.of(relatedObjectTypeSchemaRecord));
        when(relatedObjectTypeFactory.create(uri, schemaUri)).thenReturn(relatedObjectType);

        assertThat(relatedObjectTypeService.findById(id), is(relatedObjectType));
    }

    @Test
    @DisplayName("findById() throws RelatedObjectTypeNotFoundException")
    void findByIdThrowsRelatedObjectTypeNotFoundException() {
        final var id = 123;

        when(relatedObjectTypeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RelatedObjectTypeNotFoundException.class, () -> relatedObjectTypeService.findById(id));

        verifyNoInteractions(relatedObjectTypeSchemaRepository);
        verifyNoInteractions(relatedObjectTypeFactory);
    }

    @Test
    @DisplayName("findById() throws RelatedObjectTypeSchemaNotFoundException")
    void findByIdThrowsRelatedObjectTypeSchemaNotFoundException() {
        final var id = 123;
        final var schemaId = 234;
        final var uri = "_uri";

        final var relatedObjectTypeRecord = new RelatedObjectTypeRecord()
                .setSchemaId(schemaId)
                .setUri(uri);

        when(relatedObjectTypeRepository.findById(id)).thenReturn(Optional.of(relatedObjectTypeRecord));
        when(relatedObjectTypeSchemaRepository.findById(schemaId)).thenReturn(Optional.empty());

        assertThrows(RelatedObjectTypeSchemaNotFoundException.class, () -> relatedObjectTypeService.findById(id));

        verifyNoInteractions(relatedObjectTypeFactory);
    }
}