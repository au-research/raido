package au.org.raid.api.service;

import au.org.raid.api.factory.record.*;
import au.org.raid.api.repository.*;
import au.org.raid.idl.raidv2.model.Contributor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ContributorService {
    private final ContributorRepository contributorRepository;
    private final ContributorPositionRepository contributorPositionRepository;
    private final ContributorPositionSchemaRepository contributorPositionSchemaRepository;
    private final ContributorRoleSchemaRepository contributorRoleSchemaRepository;
    private final ContributorRoleRepository contributorRoleRepository;
    private final RaidContributorRepository raidContributorRepository;
    private final RaidContributorPositionRepository raidContributorPositionRepository;
    private final RaidContributorRoleRepository raidContributorRoleRepository;
    private final ContributorRecordFactory contributorRecordFactory;
    private final ContributorPositionRecordFactory contributorPositionRecordFactory;
    private final RaidContributorRecordFactory raidContributorRecordFactory;
    private final RaidContributorPositionRecordFactory raidContributorPositionRecordFactory;
    private final RaidContributorRoleRecordFactory raidContributorRoleRecordFactory;

    public void create(final List<Contributor> contributors, final String handle) {
        for (var contributor : contributors) {
            final var contributorRecord = contributorRecordFactory.create(contributor);
            final var contributorId = contributorRepository.findOrCreate(contributorRecord).getId();

            // raid contributor
            final var raidContributorRecord = raidContributorRecordFactory.create(contributor, contributorId, handle);
            final var raidContributorId = raidContributorRepository.create(raidContributorRecord).getId();

            // raid contributor position
            for (final var raidPosition : contributor.getPosition()) {
                final var positionSchema = contributorPositionSchemaRepository.findByUri(raidPosition.getSchemaUri())
                        .orElseThrow(() ->
                                new RuntimeException("Position schema not found %s".formatted(raidPosition.getSchemaUri())));

                final var position = contributorPositionRepository.findByUriAndSchemaId(raidPosition.getId(), positionSchema.getId())
                        .orElseThrow(() ->
                                new RuntimeException("Position not found %s with schema %s".formatted(raidPosition.getId(), raidPosition.getSchemaUri())));

                final var raidContributorPositionRecord = raidContributorPositionRecordFactory.create(
                        raidPosition, raidContributorId, position.getId());
                raidContributorPositionRepository.create(raidContributorPositionRecord);
            }

            // raid contributor role
            for (final var contributorRole : contributor.getRole()) {
                final var roleSchema = contributorRoleSchemaRepository.findByUri(contributorRole.getSchemaUri())
                        .orElseThrow(() ->
                                new RuntimeException("Role schema not found %s".formatted(contributorRole.getSchemaUri())));

                final var role = contributorRoleRepository.findByUriAndSchemaId(contributorRole.getId(), roleSchema.getId())
                        .orElseThrow(() ->
                                new RuntimeException("Role not found %s with schema %s".formatted(contributorRole.getId(), contributorRole.getSchemaUri())));

                final var raidContributorRoleRecord = raidContributorRoleRecordFactory.create(
                        raidContributorId, roleSchema.getId()
                );
            }




        }




    }





}
