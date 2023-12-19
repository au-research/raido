package au.org.raid.api.model.datacite;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataciteTypes {

    private String ris;
    private String bibtex;
    private String citeproc;
    private String schemaOrg;
    private String resourceType;
    private String resourceTypeGeneral;

    public DataciteTypes() {
    }

    public DataciteTypes resourceType(String resourceType) {
        this.resourceType = resourceType;
        return this;
    }

    public DataciteTypes resourceTypeGeneral(String resourceTypeGeneral) {
        this.resourceTypeGeneral = resourceTypeGeneral;
        return this;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceTypeGeneral() {
        return resourceTypeGeneral;
    }

}
