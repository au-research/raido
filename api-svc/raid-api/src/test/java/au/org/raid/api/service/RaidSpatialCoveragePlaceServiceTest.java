package au.org.raid.api.service;

import au.org.raid.api.factory.SpatialCoveragePlaceFactory;
import au.org.raid.api.factory.record.RaidSpatialCoveragePlaceRecordFactory;
import au.org.raid.api.repository.RaidSpatialCoveragePlaceRepository;
import au.org.raid.db.jooq.tables.records.RaidSpatialCoveragePlaceRecord;
import au.org.raid.idl.raidv2.model.Language;
import au.org.raid.idl.raidv2.model.SpatialCoveragePlace;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RaidSpatialCoveragePlaceServiceTest {
    @Mock
    private RaidSpatialCoveragePlaceRecordFactory raidSpatialCoveragePlaceRecordFactory;
    @Mock
    private RaidSpatialCoveragePlaceRepository raidSpatialCoveragePlaceRepository;
    @Mock
    private LanguageService languageService;
    @Mock
    private SpatialCoveragePlaceFactory spatialCoveragePlaceFactory;
    @InjectMocks
    private RaidSpatialCoveragePlaceService raidSpatialCoveragePlaceService;

    @Test
    void findAllByRaidSpatialCoverageId() {
        final var raidSpatialCoverageId = 123;
        final var languageId = 234;
        final var place = "_place";

        final var raidSpatialCoveragePlaceRecord = new RaidSpatialCoveragePlaceRecord()
                .setLanguageId(languageId)
                .setPlace(place);

        final var language = new Language();
        final var spatialCoveragePlace = new SpatialCoveragePlace();

        when(raidSpatialCoveragePlaceRepository.findAllByRaidSpatialCoverageId(raidSpatialCoverageId))
                .thenReturn(List.of(raidSpatialCoveragePlaceRecord));

        when(languageService.findById(languageId)).thenReturn(language);

        when(spatialCoveragePlaceFactory.create(place, language)).thenReturn(spatialCoveragePlace);

        assertThat(raidSpatialCoveragePlaceService.findAllByRaidSpatialCoverageId(raidSpatialCoverageId),
                is(List.of(spatialCoveragePlace)));
    }

    @Test
    @DisplayName("create() saves spatial coverage place")
    void create() {
        final var languageId = 123;
        final var raidSpatialCoverageId = 123;
        final var language = new Language();
        final var text = "_text";
        final var place = new SpatialCoveragePlace()
                .language(language)
                .text(text);

        final var record = new RaidSpatialCoveragePlaceRecord();

        when(languageService.findLanguageId(language)).thenReturn(languageId);
        when(raidSpatialCoveragePlaceRecordFactory.create(raidSpatialCoverageId, text, languageId)).thenReturn(record);

        raidSpatialCoveragePlaceService.create(List.of(place), raidSpatialCoverageId);

        verify(raidSpatialCoveragePlaceRepository).create(record);
    }
}