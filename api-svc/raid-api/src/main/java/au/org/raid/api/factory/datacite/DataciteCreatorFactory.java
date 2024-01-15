package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteCreator;
import au.org.raid.idl.raidv2.model.RegistrationAgency;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataciteCreatorFactory {
    public DataciteCreator create(final RegistrationAgency registrationAgency) {
        return Optional.ofNullable(registrationAgency)
                .map(ra -> new DataciteCreator(ra.getId()))
                .orElse(null);
    }
}
