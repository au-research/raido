package au.org.raid.api.service;

import au.org.raid.api.factory.ContributorPositionFactory;
import au.org.raid.api.repository.ContributorPositionRepository;
import au.org.raid.api.repository.ContributorPositionSchemaRepository;
import au.org.raid.api.repository.RaidContributorPositionRepository;
import au.org.raid.idl.raidv2.model.ContributorPosition;
import au.org.raid.idl.raidv2.model.ContributorPositionWithSchemaUri;
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
    private final ContributorPositionFactory contributorPositionFactory;
    private final ContributorPositionRepository contributorPositionRepository;
    private final ContributorPositionSchemaRepository contributorPositionSchemaRepository;

    public List<ContributorPositionWithSchemaUri> findAllByRaidContributorId(final Integer raidContributorId) {
        final var contributorPositions = new ArrayList<ContributorPositionWithSchemaUri>();

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
}
