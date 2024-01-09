package au.org.raid.api.service;

import au.org.raid.api.exception.RelatedObjectTypeNotFoundException;
import au.org.raid.api.exception.RelatedObjectTypeSchemaNotFoundException;
import au.org.raid.api.factory.RelatedObjectTypeFactory;
import au.org.raid.api.repository.RelatedObjectTypeRepository;
import au.org.raid.api.repository.RelatedObjectTypeSchemaRepository;
import au.org.raid.idl.raidv2.model.RelatedObjectType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RelatedObjectTypeService {
    private final RelatedObjectTypeRepository relatedObjectTypeRepository;
    private final RelatedObjectTypeSchemaRepository relatedObjectTypeSchemaRepository;
    private final RelatedObjectTypeFactory relatedObjectTypeFactory;

    public RelatedObjectType findById(final Integer id) {
        final var typeRecord = relatedObjectTypeRepository.findById(id)
                .orElseThrow(() -> new RelatedObjectTypeNotFoundException(id));

        final var typeSchemaId = typeRecord.getSchemaId();
        final var typeSchemaRecord = relatedObjectTypeSchemaRepository.findById(typeSchemaId)
                .orElseThrow(() -> new RelatedObjectTypeSchemaNotFoundException(typeSchemaId));

        return relatedObjectTypeFactory.create(typeRecord.getUri(), typeSchemaRecord.getUri());
    }
}
