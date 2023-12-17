package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteCreator;
import au.org.raid.idl.raidv2.model.RegistrationAgency;
import org.springframework.stereotype.Component;

@Component
public class DataciteCreatorFactory {
    public DataciteCreator create(final RegistrationAgency registrationAgency){
        if (registrationAgency == null) {
            return null;
        }
        DataciteCreator dataciteCreatorResult = new DataciteCreator();
        dataciteCreatorResult.dataciteCreator(registrationAgency.getId());
        return dataciteCreatorResult;
    }
}