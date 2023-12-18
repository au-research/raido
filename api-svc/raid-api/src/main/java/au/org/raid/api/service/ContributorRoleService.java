package au.org.raid.api.service;

import au.org.raid.api.factory.ContributorPositionFactory;
import au.org.raid.api.factory.ContributorRoleFactory;
import au.org.raid.api.repository.*;
import au.org.raid.idl.raidv2.model.ContributorPositionWithSchemaUri;
import au.org.raid.idl.raidv2.model.ContributorRoleWithSchemaUri;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ContributorRoleService {
    private final RaidContributorRoleRepository raidContributorRoleRepository;
    private final ContributorRoleFactory contributorRoleFactory;
    private final ContributorRoleRepository contributorRoleRepository;
    private final ContributorRoleSchemaRepository contributorRoleSchemaRepository;

    public List<ContributorRoleWithSchemaUri> findAllByRaidContributorId(final Integer raidContributorId) {
        final var contributorRoles = new ArrayList<ContributorRoleWithSchemaUri>();

        final var raidContributorRoles = raidContributorRoleRepository.findAllByRaidContributorId(raidContributorId);

        for (final var raidContributorRole : raidContributorRoles) {
            final var contributorRoleId = raidContributorRole.getContributorRoleId();

            final var contributorRoleRecord = contributorRoleRepository
                    .findById(raidContributorRole.getContributorRoleId())
                    .orElseThrow(() -> new RuntimeException(
                            "Contributor role not found with id %d".formatted(contributorRoleId)));

            final var schemaId = contributorRoleRecord.getSchemaId();

            final var contributorRoleSchemaRecord = contributorRoleSchemaRepository.findById(schemaId)
                    .orElseThrow(() -> new RuntimeException(
                            "Contributor role schema not found with id %d".formatted(schemaId)));

            contributorRoles.add(contributorRoleFactory.create(
                    contributorRoleRecord.getUri(),
                    contributorRoleSchemaRecord.getUri())
            );
        }
        return contributorRoles;
    }
}
