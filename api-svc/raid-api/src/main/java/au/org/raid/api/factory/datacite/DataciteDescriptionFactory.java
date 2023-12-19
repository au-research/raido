package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteDescription;
import au.org.raid.api.model.datacite.DataciteTitle;
import au.org.raid.idl.raidv2.model.Description;
import au.org.raid.idl.raidv2.model.Title;
import org.springframework.stereotype.Component;

@Component
public class DataciteDescriptionFactory {
    public DataciteDescription create(final Description raidDescription){
        if (raidDescription == null) {
            return null;
        }

        DataciteDescription dataciteDescriptionResult;

        String description = (raidDescription.getText() != null) ? raidDescription.getText() : "";
        String descriptionType = "Abstract";

        dataciteDescriptionResult = new DataciteDescription()
                .description(description)
                .descriptionType(descriptionType);

        return dataciteDescriptionResult;
    }
}
