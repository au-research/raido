package au.org.raid.api.service;

import au.org.raid.api.factory.record.RaidRelatedObjectRecordFactory;
import au.org.raid.api.factory.record.RelatedObjectRecordFactory;
import au.org.raid.api.repository.*;
import au.org.raid.idl.raidv2.model.RelatedObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RelatedObjectService {
    private final RelatedObjectTypeSchemaRepository relatedObjectTypeSchemaRepository;
    private final RelatedObjectTypeRepository relatedObjectTypeRepository;
    private final RelatedObjectSchemaRepository relatedObjectSchemaRepository;
    private final RelatedObjectRepository relatedObjectRepository;
    private final RaidRelatedObjectRepository raidRelatedObjectRepository;
    private final RelatedObjectRecordFactory relatedObjectRecordFactory;
    private final RaidRelatedObjectRecordFactory raidRelatedObjectRecordFactory;
    private final RaidRelatedObjectCategoryService raidRelatedObjectCategoryService;

    public void create(final List<RelatedObject> relatedObjects, final String handle) {
        if (relatedObjects == null) {
            return;
        }

        for (final var relatedObject : relatedObjects) {
            final var relatedObjectSchemaRecord = relatedObjectSchemaRepository.findByUri(relatedObject.getSchemaUri())
                    .orElseThrow(() -> new RuntimeException(
                            "Related object schema not found %s".formatted(relatedObject.getSchemaUri())));

            final var relatedObjectRecord =
                    relatedObjectRecordFactory.create(relatedObject.getId(), relatedObjectSchemaRecord.getId());

            final var savedRelatedObjectRecord = relatedObjectRepository.findOrCreate(relatedObjectRecord);

            final var relatedObjectTypeSchemaRecord =
                    relatedObjectTypeSchemaRepository.findByUri(relatedObject.getSchemaUri())
                            .orElseThrow(() -> new RuntimeException(
                                    "Related object schema not found %s".formatted(relatedObject.getSchemaUri())));

            final var relatedObjectTypeRecord = relatedObjectTypeRepository.findByUriAndSchemaId(
                    relatedObject.getId(),
                    relatedObjectTypeSchemaRecord.getId()
            ).orElseThrow(() -> new RuntimeException("Related object type %s not found in schema %s"
                    .formatted(relatedObject.getId(), relatedObject.getSchemaUri())));

            final var raidRelatedObjectRecord = raidRelatedObjectRecordFactory.create(
                    handle, savedRelatedObjectRecord.getId(), relatedObjectTypeRecord.getId());

            raidRelatedObjectRepository.create(raidRelatedObjectRecord);

            for (final var category : relatedObject.getCategory()) {
                raidRelatedObjectCategoryService.create(category, raidRelatedObjectRecord.getId());
            }
        }
    }
}
