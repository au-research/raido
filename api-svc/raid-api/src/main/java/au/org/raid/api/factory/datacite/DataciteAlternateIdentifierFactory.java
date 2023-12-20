package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteAlternateIdentifier;
import au.org.raid.idl.raidv2.model.AlternateIdentifier;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataciteAlternateIdentifierFactory {

    public DataciteAlternateIdentifier create(@Nullable final AlternateIdentifier raidAlternateIdentifier) {
        return Optional.ofNullable(raidAlternateIdentifier)
                .map(raidAltId -> new DataciteAlternateIdentifier(
                        Optional.ofNullable(raidAltId.getId()).orElse(""),
                        Optional.ofNullable(raidAltId.getType()).orElse("")))
                .orElse(null);
    }
}
