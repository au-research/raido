package au.org.raid.api.service;

import au.org.raid.api.factory.AccessTypeFactory;
import au.org.raid.api.repository.AccessTypeRepository;
import au.org.raid.api.repository.AccessTypeSchemaRepository;
import au.org.raid.idl.raidv2.model.AccessType;
import au.org.raid.idl.raidv2.model.AccessTypeWithSchemaUri;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccessTypeService {
    private final AccessTypeRepository accessTypeRepository;
    private final AccessTypeSchemaRepository accessTypeSchemaRepository;
    private final AccessTypeFactory accessTypeFactory;
    public AccessTypeWithSchemaUri findById(final Integer id) {
        final var record = accessTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Access type not found with id %d".formatted(id)));

        final var schemaId = record.getSchemaId();

        final var schemaRecord = accessTypeSchemaRepository.findById(schemaId)
                .orElseThrow(() -> new RuntimeException("Access type schema not found with id %d".formatted(schemaId)));

        return accessTypeFactory.create(record.getUri(), schemaRecord.getUri());
    }
}
