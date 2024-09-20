package au.org.raid.api.service;

import au.org.raid.api.factory.RaidListenerMessageFactory;
import au.org.raid.idl.raidv2.model.Contributor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RaidListenerService {
    private final RaidListenerClient raidListenerClient;
    private final RaidListenerMessageFactory raidListenerMessageFactory;

    public void create(final String handle, final List<Contributor> contributors) {
        contributors
                .forEach(contributor -> {
                    contributor.uuid(UUID.randomUUID().toString());
                    contributor.setStatus("PENDING");
                    final var message = raidListenerMessageFactory.create(
                            handle,
                            contributor
                    );
                    raidListenerClient.post(message);

                    contributor.email(null);
                });
    }

    public void update(final String handle, final List<Contributor> contributors, final List<Contributor> existingContributors) {
        final var contributorsToCreate = contributors.stream()
                .filter(contributor -> contributor.getEmail() != null)
                .toList();

        final var contributorsToUpdate = contributors.stream()
                .filter(contributor -> contributor.getUuid() != null)
                .filter(contributor -> {
                    final var existingContributorOptional = existingContributors.stream().filter(c -> c.getUuid().equals(contributor.getUuid())).findFirst();

                    // ignore if existing contributor has an orcid and it's status us 'confirmed'

                    if (existingContributorOptional.isPresent()) {
                        final var existingContributor = existingContributorOptional.get();
                        return (existingContributor.getId() == null || !existingContributor.getStatus().equalsIgnoreCase("confirmed"));
                    }
                    return false;
                })
                .toList();

        contributorsToCreate
                .forEach(contributor -> {
                    final var message = raidListenerMessageFactory.create(
                            handle,
                            contributor
                    );
                    raidListenerClient.post(message);
                });

        contributorsToUpdate
                .forEach(contributor -> {
                    final var message = raidListenerMessageFactory.create(
                            handle,
                            contributor
                    );
                    raidListenerClient.post(message);
                });
    }
}
