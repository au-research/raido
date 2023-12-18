package au.org.raid.api.service;

import au.org.raid.api.factory.AlternateUrlFactory;
import au.org.raid.api.factory.record.RaidAlternateUrlRecordFactory;
import au.org.raid.api.repository.RaidAlternateUrlRepository;
import au.org.raid.idl.raidv2.model.AlternateUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AlternateUrlService {
    private final RaidAlternateUrlRepository raidAlternateUrlRepository;
    private final RaidAlternateUrlRecordFactory raidAlternateUrlRecordFactory;
    private final AlternateUrlFactory alternateUrlFactory;

    public void create(final List<AlternateUrl> alternateUrls, final String handle) {
        if (alternateUrls == null) {
            return;
        }

        for (final var alternateUrl : alternateUrls) {
            final var raidAlternateUrlRecord = raidAlternateUrlRecordFactory.create(alternateUrl, handle);
            raidAlternateUrlRepository.create(raidAlternateUrlRecord);
        }
    }

    public List<AlternateUrl> findAllByHandle(final String handle) {
        final var alternateUrls = new ArrayList<AlternateUrl>();
        final var records = raidAlternateUrlRepository.findAllByHandle(handle);

        for (final var record : records) {
            alternateUrls.add(alternateUrlFactory.create(record.getUrl()));
        }
        return alternateUrls;
    }
}
