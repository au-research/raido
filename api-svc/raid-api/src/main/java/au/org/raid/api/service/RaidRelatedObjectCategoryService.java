package au.org.raid.api.service;

import au.org.raid.api.factory.record.RaidRelatedObjectCategoryRecordFactory;
import au.org.raid.api.repository.RaidRelatedObjectCategoryRepository;
import au.org.raid.api.repository.RelatedObjectCategorySchemaRepository;
import au.org.raid.api.repository.RelatedObjectCategoryRepository;
import au.org.raid.idl.raidv2.model.RelatedObjectCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RaidRelatedObjectCategoryService {
    private final RelatedObjectCategorySchemaRepository relatedObjectCategorySchemaRepository;
    private final RelatedObjectCategoryRepository relatedObjectCategoryRepository;
    private final RaidRelatedObjectCategoryRepository raidRelatedObjectCategoryRepository;
    private final RaidRelatedObjectCategoryRecordFactory raidRelatedObjectCategoryRecordFactory;

    public void create(final RelatedObjectCategory category, final Integer raidRelatedObjectId) {
        final var schemaRecord = relatedObjectCategorySchemaRepository.findByUri(category.getSchemaUri())
                .orElseThrow(() -> new RuntimeException(
                        "Related object category schema not found %s".formatted(category.getSchemaUri())));

        final var categoryRecord = relatedObjectCategoryRepository
                .findByUriAndSchemaId(category.getId(), schemaRecord.getId())
                .orElseThrow(() -> new RuntimeException(
                        "Related object category %s not found in schema %s".formatted(category.getId(), category.getSchemaUri())));

        final var raidRelatedObjectCategoryRecord =
                raidRelatedObjectCategoryRecordFactory.create(raidRelatedObjectId, categoryRecord.getId());

        raidRelatedObjectCategoryRepository.create(raidRelatedObjectCategoryRecord);
    }
}
