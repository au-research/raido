package au.org.raid.api.service;

import au.org.raid.api.dto.RaidListenerMessage;
import au.org.raid.api.factory.RaidListenerMessageFactory;
import au.org.raid.idl.raidv2.model.Contributor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RaidListenerServiceTest {
    @Mock
    private RaidListenerClient raidListenerClient;
    @Mock
    private RaidListenerMessageFactory raidListenerMessageFactory;

    @InjectMocks
    private RaidListenerService raidListenerService;

    @Test
    @DisplayName("Sends contributors to raid listener on create")
    void create() {
        final var handle = "_handle";
        final var email = "_email";
        final var message = new RaidListenerMessage();
        final var contributor = new Contributor().email(email);

        when(raidListenerMessageFactory.create(handle, contributor))
                .thenReturn(message);

        raidListenerService.create(handle, List.of(contributor));

        verify(raidListenerMessageFactory).create(handle, contributor);
        verify(raidListenerClient).post(message);
    }

    @Test
    @DisplayName("Creates contributors on update")
    void createsContributorsOnUpdate() {
        final var handle = "_handle";
        final var email = "_email";
        final var contributor = new Contributor().email(email);
        final var message = new RaidListenerMessage();

        when(raidListenerMessageFactory.create(handle, contributor))
                .thenReturn(message);

        raidListenerService.update(handle, List.of(contributor), Collections.emptyList());

        verifyNoMoreInteractions(raidListenerMessageFactory);
        verify(raidListenerClient).post(message);
        verifyNoMoreInteractions(raidListenerClient);
    }

    @Test
    @DisplayName("Updates contributors on update")
    void updatesContributorsOnUpdate() {
        final var handle = "_handle";
        final var uuid = "_uuid";
        final var contributor = new Contributor().uuid(uuid).contact(true);
        final var existingContributor = new Contributor().uuid(uuid).contact(false);
        final var message = new RaidListenerMessage();

        when(raidListenerMessageFactory.create(handle, contributor))
                .thenReturn(message);

        raidListenerService.update(handle, List.of(contributor), List.of(existingContributor));

        verifyNoMoreInteractions(raidListenerMessageFactory);
        verify(raidListenerClient).post(message);
        verifyNoMoreInteractions(raidListenerClient);
    }

    @Test
    @DisplayName("Ignores confirmed contributors")
    void ignoresUnchangedContributors() {
        final var handle = "_handle";
        final var uuid = "_uuid";
        final var id = "_id";
        final var status = "confirmed";
        final var contributor = new Contributor()
                .id(id)
                .status(status)
                .uuid(uuid)
                .contact(true);

        final var existingContributor = new Contributor()
                .id(id)
                .status(status)
                .uuid(uuid)
                .contact(false);

        raidListenerService.update(handle, List.of(contributor), List.of(existingContributor));

        verifyNoInteractions(raidListenerMessageFactory);
        verifyNoInteractions(raidListenerClient);
    }
}