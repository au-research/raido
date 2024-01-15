package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteDescription;
import au.org.raid.idl.raidv2.model.Description;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataciteDescriptionFactory {

    public DataciteDescription create(final Description raidDescription) {
        return Optional.ofNullable(raidDescription)
                .map(desc -> new DataciteDescription()
                        .setDescription(Optional.ofNullable(desc.getText()).orElse(""))
                        .setDescriptionType("Abstract"))
                .orElse(null);
    }
}
