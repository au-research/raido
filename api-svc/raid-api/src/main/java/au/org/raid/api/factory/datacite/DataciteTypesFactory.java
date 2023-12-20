package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteTitle;
import au.org.raid.api.model.datacite.DataciteTypes;
import au.org.raid.idl.raidv2.model.Title;
import org.springframework.stereotype.Component;

@Component
public class DataciteTypesFactory {
    public DataciteTypes create(final String resourceType, final String resourceTypeGeneral){
        if (resourceType == null && resourceTypeGeneral == null) {
            return null;
        }

        DataciteTypes dataciteTypes = new DataciteTypes();

        dataciteTypes.setResourceType("RAiD Project");
        dataciteTypes.setResourceTypeGeneral("Other");

        return dataciteTypes;
    }
}
