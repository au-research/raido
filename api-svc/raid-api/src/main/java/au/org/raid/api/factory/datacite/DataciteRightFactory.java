package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteRight;
import au.org.raid.api.model.datacite.DataciteTitle;
import au.org.raid.idl.raidv2.model.Access;
import au.org.raid.idl.raidv2.model.Id;
import au.org.raid.idl.raidv2.model.Title;
import org.springframework.stereotype.Component;

@Component
public class DataciteRightFactory {
    public DataciteRight create(final Access raidAccess, final Id raidId){
        if (raidAccess == null) {
            return null;
        }

        DataciteRight dataciteRightResult;

        String rights = (raidId.getLicense() != null) ? raidId.getLicense() : "";
        String rightsURI = (raidId.getLicense() != null) ? raidId.getLicense() : "";

        dataciteRightResult = new DataciteRight().rights(rights).rightsURI(rightsURI);

        return dataciteRightResult;
    }
}
