package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.AlternateIdentifier;
import org.springframework.stereotype.Component;

@Component
public class AlternateIdentifierFactory {
    public AlternateIdentifier create(final String id, final String type) {
        return new AlternateIdentifier()
                .id(id)
                .type(type);
    }
}