package au.org.raid.api.service;

import au.org.raid.api.factory.ContributorPositionFactory;
import au.org.raid.api.factory.record.RaidContributorPositionRecordFactory;
import au.org.raid.api.repository.ContributorPositionRepository;
import au.org.raid.api.repository.ContributorPositionSchemaRepository;
import au.org.raid.api.repository.RaidContributorPositionRepository;
import au.org.raid.idl.raidv2.model.ContributorPosition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ContributorPositionService {
    private final RaidContributorPositionRepository raidContributorPositionRepository;
    private final RaidContributorPositionRecordFactory raidContributorPositionRecordFactory;
    private final ContributorPositionFactory contributorPositionFactory;
    private final ContributorPositionRepository contributorPositionRepository;
    private final ContributorPositionSchemaRepository contributorPositionSchemaRepository;

    public List<ContributorPosition> findAllByRaidContributorId(final Integer raidContributorId) {
        final var contributorPositions = new ArrayList<ContributorPosition>();

        final var raidContributorPositions =
                raidContributorPositionRepository.findAllByRaidContributorId(raidContributorId);

        for (final var raidContributorPosition : raidContributorPositions) {
            final var contributorPositionId = raidContributorPosition.getContributorPositionId();

            final var contributorPositionRecord = contributorPositionRepository
                    .findById(raidContributorPosition.getContributorPositionId())
                    .orElseThrow(() -> new RuntimeException(
                            "Contributor position not found with id %d".formatted(contributorPositionId)));

            final var schemaId = contributorPositionRecord.getSchemaId();

            final var contributorPositionSchemaRecord = contributorPositionSchemaRepository.findById(schemaId)
                    .orElseThrow(() -> new RuntimeException(
                            "Contributor position schema not found with id %d".formatted(schemaId)));

            contributorPositions.add(contributorPositionFactory.create(
                    raidContributorPosition,
                    contributorPositionRecord.getUri(),
                    contributorPositionSchemaRecord.getUri())
            );
        }
        return contributorPositions;
    }

    public void create(final List<ContributorPosition> positions, final int raidContributorId) {
        for (final var position : positions) {
            final var positionSchema = contributorPositionSchemaRepository.findByUri(position.getSchemaUri())
                    .orElseThrow(() ->
                            new RuntimeException("Position schema not found %s".formatted(position.getSchemaUri())));

            final var contributorPosition = contributorPositionRepository.findByUriAndSchemaId(position.getId(), positionSchema.getId())
                    .orElseThrow(() ->
                            new RuntimeException("Position not found %s with schema %s".formatted(position.getId(), position.getSchemaUri())));

            final var raidContributorPositionRecord = raidContributorPositionRecordFactory.create(
                    position, raidContributorId, contributorPosition.getId());

            raidContributorPositionRepository.create(raidContributorPositionRecord);
        }
    }
}
