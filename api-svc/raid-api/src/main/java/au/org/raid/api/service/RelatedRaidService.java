package au.org.raid.api.service;

import au.org.raid.api.factory.record.RelatedRaidRecordFactory;
import au.org.raid.api.repository.RelatedRaidRepository;
import au.org.raid.api.repository.RelatedRaidTypeRepository;
import au.org.raid.api.repository.RelatedRaidTypeSchemaRepository;
import au.org.raid.idl.raidv2.model.RelatedRaid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RelatedRaidService {
    private final RelatedRaidTypeSchemaRepository relatedRaidTypeSchemaRepository;
    private final RelatedRaidTypeRepository relatedRaidTypeRepository;
    private final RelatedRaidRepository relatedRaidRepository;
    private final RelatedRaidRecordFactory relatedRaidRecordFactory;
    public void create(final List<RelatedRaid> relatedRaids, final String raidName) {
        for (final var relatedRaid : relatedRaids) {
            final var relatedRaidType = relatedRaid.getType();

            final var relatedRaidTypeSchemaRecord = relatedRaidTypeSchemaRepository.findByUri(relatedRaidType.getSchemaUri())
                    .orElseThrow(() -> new RuntimeException(
                            "Related raid type schema not found %s".formatted(relatedRaidType.getSchemaUri())));

            final var relatedRaidTypeRecord = relatedRaidTypeRepository.findByUriAndSchemaId(
                            relatedRaidType.getId(), relatedRaidTypeSchemaRecord.getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Related raid type %s not found in schema %s".formatted(relatedRaidType.getId(), relatedRaidType.getSchemaUri())));

            final var relatedRaidRecord = relatedRaidRecordFactory.create(
                    raidName, relatedRaid.getId(), relatedRaidTypeRecord.getId());

            relatedRaidRepository.create(relatedRaidRecord);
        }
    }
}