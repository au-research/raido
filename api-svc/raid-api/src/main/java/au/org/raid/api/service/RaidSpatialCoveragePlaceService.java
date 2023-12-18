package au.org.raid.api.service;

import au.org.raid.api.factory.SpatialCoveragePlaceFactory;
import au.org.raid.api.factory.record.RaidSpatialCoveragePlaceRecordFactory;
import au.org.raid.api.repository.RaidSpatialCoveragePlaceRepository;
import au.org.raid.idl.raidv2.model.SpatialCoveragePlace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RaidSpatialCoveragePlaceService {
    private final RaidSpatialCoveragePlaceRecordFactory raidSpatialCoveragePlaceRecordFactory;
    private final RaidSpatialCoveragePlaceRepository raidSpatialCoveragePlaceRepository;
    private final LanguageService languageService;
    private final SpatialCoveragePlaceFactory spatialCoveragePlaceFactory;

    public List<SpatialCoveragePlace> findAllByRaidSpatialCoverageId(final Integer raidSpatialCoverageId) {
        final var places = new ArrayList<SpatialCoveragePlace>();
        final var records = raidSpatialCoveragePlaceRepository.findAllByRaidSpatialCoverageId(raidSpatialCoverageId);

        for (final var record : records) {
            final var language = languageService.findById(record.getLanguageId());
            places.add(spatialCoveragePlaceFactory.create(record.getPlace(), language));
        }

        return places;
    }

    public void create(final List<SpatialCoveragePlace> places, final Integer raidSpatialCoverageId) {
        for (final var place : places) {
            final var languageId = languageService.findLanguageId(place.getLanguage());
            final var record = raidSpatialCoveragePlaceRecordFactory.create(raidSpatialCoverageId, place.getText(), languageId);

            raidSpatialCoveragePlaceRepository.create(record);
        }
    }
}
