package au.org.raid.api.model.datacite;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataciteRight {

    @JsonProperty("rights")
    private String rights;

    @JsonProperty("rightsUri")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String rightsURI;


    public DataciteRight() {
    }

    public DataciteRight rights(String rights) {
        this.rights = rights;
        return this;
    }

    public DataciteRight rightsURI(String rightsURI) {
        this.rightsURI = rightsURI;
        return this;
    }

}
