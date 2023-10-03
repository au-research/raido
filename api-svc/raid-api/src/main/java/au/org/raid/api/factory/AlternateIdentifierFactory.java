package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.AlternateIdentifier;
import au.org.raid.idl.raidv2.model.AlternateIdentifierBlock;
import org.springframework.stereotype.Component;

@Component
public class AlternateIdentifierFactory {
    public AlternateIdentifier create(final AlternateIdentifierBlock alternateIdentifierBlock) {
        if (alternateIdentifierBlock == null) {
            return null;
        }

        return new AlternateIdentifier()
                .id(alternateIdentifierBlock.getAlternateIdentifier())
                .type(alternateIdentifierBlock.getAlternateIdentifierType());
    }
}