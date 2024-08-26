package au.org.raid.api.service;

import au.org.raid.api.factory.RaidListenerMessageFactory;
import au.org.raid.api.service.raid.RaidChecksumService;
import au.org.raid.idl.raidv2.model.Contributor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RaidListenerService {
    private final RaidListenerClient raidListenerClient;
    private final RaidListenerMessageFactory raidListenerMessageFactory;
    private final RaidChecksumService checksumService;

    public void create(final String handle, final List<Contributor> contributors) {
        contributors
                .forEach(contributor -> {
                    contributor.uuid(UUID.randomUUID().toString());
                    contributor.setStatus("PENDING");
                    final var message = raidListenerMessageFactory.create(
                            handle,
                            contributor,
                            false
                    );
                    raidListenerClient.post(message);

                    contributor.email(null);
                });
    }

    public void update(final String handle, final List<Contributor> contributors, final List<Contributor> existingContributors) {
        final var contributorsToCreate = contributors.stream()
                .filter(contributor -> contributor.getEmail() != null)
                .toList();

        final var requestOrcids = contributors.stream()
                //TODO: check orcid exists in contributor table
                .map(Contributor::getId)
                .filter(Objects::nonNull)
                .toList();

        final var contributorsToDelete = existingContributors.stream()
                .filter(contributor -> contributor.getId() != null)
                .filter(contributor -> !requestOrcids.contains(contributor.getId()))
                .toList();

        final var contributorsToUpdate = new ArrayList<Contributor>();

        for (final var contributor : contributors) {
            final var extant = existingContributors.stream().filter(c -> c.getUuid().equals(contributor.getUuid())).findFirst();

            if (extant.isPresent()) {
                final var contributorChecksum = checksumService.create(contributor);
                final var extantChecksum = checksumService.create(extant.get());

                if (!contributorChecksum.equals(extantChecksum)) {
                    contributorsToUpdate.add(contributor);
                }
            }
        }

        contributorsToCreate
                .forEach(contributor -> {
                    final var message = raidListenerMessageFactory.create(
                            handle,
                            contributor,
                            false
                    );
                    raidListenerClient.post(message);
                });

        contributorsToDelete
                .forEach(contributor -> {
                    final var message = raidListenerMessageFactory.create(
                            handle,
                            contributor,
                            true
                    );
                    raidListenerClient.post(message);
                });

        contributorsToUpdate
                .forEach(contributor -> {
                    final var message = raidListenerMessageFactory.create(
                            handle,
                            contributor,
                            false
                    );
                    raidListenerClient.post(message);
                });
    }
}
