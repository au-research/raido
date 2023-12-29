package au.org.raid.api.service;

import au.org.raid.api.exception.AccessTypeNotFoundException;
import au.org.raid.api.exception.AccessTypeSchemaNotFoundException;
import au.org.raid.api.factory.AccessTypeFactory;
import au.org.raid.api.repository.AccessTypeRepository;
import au.org.raid.api.repository.AccessTypeSchemaRepository;
import au.org.raid.db.jooq.tables.records.AccessTypeRecord;
import au.org.raid.db.jooq.tables.records.AccessTypeSchemaRecord;
import au.org.raid.idl.raidv2.model.AccessType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessTypeServiceTest {
    @Mock
    private AccessTypeRepository accessTypeRepository;
    @Mock
    private AccessTypeSchemaRepository accessTypeSchemaRepository;
    @Mock
    private AccessTypeFactory accessTypeFactory;
    @InjectMocks
    private AccessTypeService accessTypeService;

    @Test
    @DisplayName("findById returns AccessType")
    void findByIdReturnsAccessType() {
        final var uri = "_uri";
        final var schemaUri = "schema-uri";
        final var id = 123;
        final var schemaId = 234;
        final var accessTypeRecord = new AccessTypeRecord()
                .setSchemaId(schemaId)
                .setUri(uri);
        final var accessTypeSchemaRecord = new AccessTypeSchemaRecord()
                .setUri(schemaUri);
        final var accessType = new AccessType();

        when(accessTypeRepository.findById((id))).thenReturn(Optional.of(accessTypeRecord));
        when(accessTypeSchemaRepository.findById(schemaId)).thenReturn(Optional.of(accessTypeSchemaRecord));
        when(accessTypeFactory.create(uri, schemaUri)).thenReturn(accessType);

        assertThat(accessTypeService.findById(id), is(accessType));
    }

    @Test
    @DisplayName("findById throws AccessTypeNoteFoundException if access ttype not found")
    void findByIdReturnsAccessTypeThrowsAccessTypeNotFoundException() {
        final var id = 123;

        when(accessTypeRepository.findById((id))).thenReturn(Optional.empty());

        assertThrows(AccessTypeNotFoundException.class, () -> accessTypeService.findById(id));
    }

    @Test
    @DisplayName("findById throws AccessTypeSchemaNotFoundException when access type schema not found")
    void findByIdThrowsAccessTypeSchemaNotFoundException() {
        final var uri = "_uri";
        final var id = 123;
        final var schemaId = 234;
        final var accessTypeRecord = new AccessTypeRecord()
                .setSchemaId(schemaId)
                .setUri(uri);

        when(accessTypeRepository.findById((id))).thenReturn(Optional.of(accessTypeRecord));
        when(accessTypeSchemaRepository.findById(schemaId)).thenReturn(Optional.empty());

        assertThrows(AccessTypeSchemaNotFoundException.class, () -> accessTypeService.findById(id));
    }
}