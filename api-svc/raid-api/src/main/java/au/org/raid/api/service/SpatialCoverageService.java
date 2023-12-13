package au.org.raid.api.service;

import au.org.raid.api.factory.record.RaidSpatialCoverageRecordFactory;
import au.org.raid.api.repository.RaidSpatialCoverageRepository;
import au.org.raid.api.repository.SpatialCoverageSchemaRepository;
import au.org.raid.idl.raidv2.model.SpatialCoverage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SpatialCoverageService {
    private final SpatialCoverageSchemaRepository spatialCoverageSchemaRepository;
    private final RaidSpatialCoverageRepository raidSpatialCoverageRepository;
    private final RaidSpatialCoverageRecordFactory raidSpatialCoverageRecordFactory;
    private final LanguageService languageService;

    public void create(final List<SpatialCoverage> spatialCoverages, final String handle) {
        if (spatialCoverages == null) {
            return;
        }

        for (final var spatialCoverage : spatialCoverages) {

            final var spatialCoverageSchemaRecord = spatialCoverageSchemaRepository.findByUri(spatialCoverage.getSchemaUri())
                    .orElseThrow(() -> new RuntimeException(
                            "Spatial coverage schema not found %s".formatted(spatialCoverage.getSchemaUri())));

            final var languageId = languageService.findLanguageId(spatialCoverage.getLanguage());

            final var raidSpatialCoverageRecord = raidSpatialCoverageRecordFactory.create(spatialCoverage, handle, spatialCoverageSchemaRecord.getId(), languageId);

            raidSpatialCoverageRepository.create(raidSpatialCoverageRecord);
        }
    }
}
