package au.org.raid.api.factory;

import au.org.raid.api.service.Handle;
import org.springframework.stereotype.Component;

@Component
public class HandleFactory {
    public Handle create(final String handle) {
        return new Handle(handle);
    }
}
