package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteTypes;
import org.springframework.stereotype.Component;

@Component
public class DataciteTypesFactory {

    public DataciteTypes create(final String resourceType, final String resourceTypeGeneral) {
        if (resourceType == null && resourceTypeGeneral == null) {
            return null;
        }

        return new DataciteTypes()
                .setResourceType(resourceType != null ? resourceType : "RAiD Project")
                .setResourceTypeGeneral(resourceTypeGeneral != null ? resourceTypeGeneral : "Other");
    }
}