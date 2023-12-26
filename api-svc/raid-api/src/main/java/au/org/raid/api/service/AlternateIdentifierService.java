package au.org.raid.api.service;

import au.org.raid.api.factory.AlternateIdentifierFactory;
import au.org.raid.api.factory.record.RaidAlternateIdentifierRecordFactory;
import au.org.raid.api.repository.RaidAlternateIdentifierRepository;
import au.org.raid.idl.raidv2.model.AlternateIdentifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AlternateIdentifierService {
    private final RaidAlternateIdentifierRepository raidAlternateIdentifierRepository;
    private final RaidAlternateIdentifierRecordFactory raidAlternateIdentifierRecordFactory;
    private final AlternateIdentifierFactory alternateIdentifierFactory;
    public void create(final List<AlternateIdentifier> alternateIdentifiers, final String handle) {
        if (alternateIdentifiers == null) {
            return;
        }

        for (final var alternateIdentifier : alternateIdentifiers) {
            final var raidAlternateIdentifierRecord = raidAlternateIdentifierRecordFactory.create(alternateIdentifier, handle);
            raidAlternateIdentifierRepository.create(raidAlternateIdentifierRecord);
        }
    }

    public List<AlternateIdentifier> findAllByHandle(final String handle) {
        final var alternateIdentifiers = new ArrayList<AlternateIdentifier>();
        final var records = raidAlternateIdentifierRepository.findAllByHandle(handle);

        for (final var record : records) {
            alternateIdentifiers.add(alternateIdentifierFactory.create(record.getId(), record.getType()));
        }

        return alternateIdentifiers;
    }

    public void update(final List<AlternateIdentifier> alternateIdentifiers, final String handle) {
        raidAlternateIdentifierRepository.deleteAllByHandle(handle);
        create(alternateIdentifiers, handle);
    }
}
