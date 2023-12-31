package au.org.raid.api.service;

import au.org.raid.api.factory.AlternateUrlFactory;
import au.org.raid.api.factory.record.RaidAlternateUrlRecordFactory;
import au.org.raid.api.repository.RaidAlternateUrlRepository;
import au.org.raid.api.util.TestRaid;
import au.org.raid.db.jooq.tables.records.RaidAlternateUrlRecord;
import au.org.raid.idl.raidv2.model.AlternateUrl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlternateUrlServiceTest {
    @Mock
    private RaidAlternateUrlRepository raidAlternateUrlRepository;
    @Mock
    private RaidAlternateUrlRecordFactory raidAlternateUrlRecordFactory;
    @Mock
    private AlternateUrlFactory alternateUrlFactory;
    @InjectMocks
    private AlternateUrlService alternateUrlService;

    @Test
    @DisplayName("Create saves all alternate urls")
    void createSavesAlternateUrl() {
        final var raidAlternateUrlRecord = new RaidAlternateUrlRecord();

        when(raidAlternateUrlRecordFactory.create(TestRaid.RAID_DTO.getAlternateUrl().get(0), TestRaid.HANDLE))
                .thenReturn(raidAlternateUrlRecord);

        alternateUrlService.create(TestRaid.RAID_DTO.getAlternateUrl(), TestRaid.HANDLE);

        verify(raidAlternateUrlRepository).create(raidAlternateUrlRecord);
    }

    @Test
    @DisplayName("Create does not save when alternate url is null")
    void createWithNullAlternateUrl() {
        try {
            alternateUrlService.create(null, TestRaid.HANDLE);
        } catch (final Exception e) {
            fail("No exception expected");
        }

        verifyNoInteractions(raidAlternateUrlRecordFactory);
        verifyNoInteractions(raidAlternateUrlRepository);
    }

    @Test
    @DisplayName("Create does not save with empty list")
    void createWithEmptyList() {
        try {
            alternateUrlService.create(Collections.emptyList(), TestRaid.HANDLE);
        } catch (final Exception e) {
            fail("No exception expected");
        }
        verifyNoInteractions(raidAlternateUrlRecordFactory);
        verifyNoInteractions(raidAlternateUrlRepository);
    }

    @Test
    @DisplayName("findAllByHandle returns all alternate urls")
    void findAllByHandleReturnsAllAlternateUrls() {
        final var url = "_url";
        final var raidAlternateUrlRecord = new RaidAlternateUrlRecord()
                .setUrl(url);
        final var alternateUrl = new AlternateUrl();

        when(raidAlternateUrlRepository.findAllByHandle(TestRaid.HANDLE)).thenReturn(List.of(raidAlternateUrlRecord));
        when(alternateUrlFactory.create(url)).thenReturn(alternateUrl);

        assertThat(alternateUrlService.findAllByHandle(TestRaid.HANDLE), is(List.of(alternateUrl)));
    }

    @Test
    @DisplayName("findAllByHandle returns empty list")
    void findAllByHandleReturnsEmptyList() {
        when(raidAlternateUrlRepository.findAllByHandle(TestRaid.HANDLE)).thenReturn(Collections.emptyList());

        assertThat(alternateUrlService.findAllByHandle(TestRaid.HANDLE), is(Collections.emptyList()));

        verifyNoInteractions(alternateUrlFactory);
    }

    @Test
    @DisplayName("update() deletes and reinserts alternate urls")
    void update() {
        final var raidAlternateUrlRecord = new RaidAlternateUrlRecord();

        when(raidAlternateUrlRecordFactory.create(TestRaid.RAID_DTO.getAlternateUrl().get(0), TestRaid.HANDLE))
                .thenReturn(raidAlternateUrlRecord);

        alternateUrlService.update(TestRaid.RAID_DTO.getAlternateUrl(), TestRaid.HANDLE);

        verify(raidAlternateUrlRepository).deleteAllByHandle(TestRaid.HANDLE);
        verify(raidAlternateUrlRepository).create(raidAlternateUrlRecord);
    }
}