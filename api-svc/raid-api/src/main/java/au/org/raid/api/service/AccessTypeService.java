package au.org.raid.api.service;

import au.org.raid.api.exception.AccessTypeNotFoundException;
import au.org.raid.api.exception.AccessTypeSchemaNotFoundException;
import au.org.raid.api.factory.AccessTypeFactory;
import au.org.raid.api.repository.AccessTypeRepository;
import au.org.raid.api.repository.AccessTypeSchemaRepository;
import au.org.raid.idl.raidv2.model.AccessType;
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
    public AccessType findById(final Integer id) {
        final var record = accessTypeRepository.findById(id)
                .orElseThrow(() -> new AccessTypeNotFoundException(id));

        final var schemaId = record.getSchemaId();

        final var schemaRecord = accessTypeSchemaRepository.findById(schemaId)
                .orElseThrow(() -> new AccessTypeSchemaNotFoundException(schemaId));

        return accessTypeFactory.create(record.getUri(), schemaRecord.getUri());
    }
}
