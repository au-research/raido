package au.org.raid.api.factory;

import au.org.raid.api.service.Handle;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class HandleFactory {
    public Handle create(final String handle) {
        return new Handle(handle);
    }

    public Handle createWithPrefix(final String prefix) {
        final var suffix = UUID.randomUUID().toString().split("-")[0];
        return new Handle(prefix, suffix);
    }
}
