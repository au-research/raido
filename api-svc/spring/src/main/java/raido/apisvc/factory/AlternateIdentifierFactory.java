package raido.apisvc.factory;

import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.AlternateIdentifier;
import raido.idl.raidv2.model.AlternateIdentifierBlock;

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