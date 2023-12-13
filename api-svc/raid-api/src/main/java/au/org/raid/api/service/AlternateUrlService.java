package au.org.raid.api.service;

import au.org.raid.api.factory.record.RaidAlternateUrlRecordFactory;
import au.org.raid.api.repository.RaidAlternateUrlRepository;
import au.org.raid.idl.raidv2.model.AlternateUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AlternateUrlService {
    private final RaidAlternateUrlRepository raidAlternateUrlRepository;
    private final RaidAlternateUrlRecordFactory raidAlternateUrlRecordFactory;

    public void create(final List<AlternateUrl> alternateUrls, final String handle) {
        if (alternateUrls == null) {
            return;
        }

        for (final var alternateUrl : alternateUrls) {
            final var raidAlternateUrlRecord = raidAlternateUrlRecordFactory.create(alternateUrl, handle);
            raidAlternateUrlRepository.create(raidAlternateUrlRecord);
        }
    }
}
