package au.org.raid.api.service;

import au.org.raid.api.factory.RaidListenerMessageFactory;
import au.org.raid.api.service.raid.RaidChecksumService;
import au.org.raid.idl.raidv2.model.Contributor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RaidListenerService {
    private final RaidListenerClient raidListenerClient;
    private final RaidListenerMessageFactory raidListenerMessageFactory;
    private final RaidChecksumService checksumService;

    public void create(final String handle, final List<Contributor> contributors) {
        contributors.stream()
                .map(Contributor::getEmail)
                .distinct()
                .forEach(email -> {
                    final var message = raidListenerMessageFactory.create(
                            handle,
                            email,
                            null,
                            contributors.stream()
                                    .filter(contributor -> contributor.getEmail().equals(email)).collect(Collectors.toList()),
                            false
                    );
                    raidListenerClient.post(message);
                });
    }

    public void update(final String handle, final List<Contributor> contributors, final List<Contributor> existingContributors) {
        final var contributorsToCreate = contributors.stream()
                .filter(contributor -> contributor.getEmail() != null)
                .toList();

        final var requestOrcids = contributors.stream()
                .map(Contributor::getId)
                .filter(Objects::nonNull)
                .toList();

        final var contributorsToDelete = existingContributors.stream()
                .filter(contributor -> !requestOrcids.contains(contributor.getId()))
                .toList();

        final var contributorsToUpdate = new ArrayList<Contributor>();

        for (final var contributor : contributors) {
            final var extant = existingContributors.stream().filter(c -> c.getId().equals(contributor.getId())).findFirst();

            if (extant.isPresent()) {
                final var contributorChecksum = checksumService.create(contributor);
                final var extantChecksum = checksumService.create(extant);

                if (!contributorChecksum.equals(extantChecksum)) {
                    contributorsToUpdate.add(contributor);
                }
            }
        }

        contributorsToCreate.stream()
                .map(Contributor::getEmail)
                .distinct()
                .forEach(email -> {
                    final var message = raidListenerMessageFactory.create(
                            handle,
                            email,
                            null,
                            contributors.stream()
                                    .filter(contributor -> contributor.getEmail().equals(email)).collect(Collectors.toList()),
                            false
                    );
                    raidListenerClient.post(message);
                });

        contributorsToDelete.stream()
                .map(Contributor::getId)
                .distinct()
                .forEach(id -> {
                    final var message = raidListenerMessageFactory.create(
                            handle,
                            null,
                            id,
                            contributors.stream()
                                    .filter(contributor -> contributor.getId().equals(id)).collect(Collectors.toList()),
                            true
                    );
                    raidListenerClient.post(message);
                });

        contributorsToUpdate.stream()
                .map(Contributor::getId)
                .distinct()
                .forEach(id -> {
                    final var message = raidListenerMessageFactory.create(
                            handle,
                            null,
                            id,
                            contributors.stream()
                                    .filter(contributor -> contributor.getId().equals(id)).collect(Collectors.toList()),
                            false
                    );
                    raidListenerClient.post(message);
                });

    }
}
