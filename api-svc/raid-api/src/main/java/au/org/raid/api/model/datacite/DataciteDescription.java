package au.org.raid.api.model.datacite;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataciteDescription {

    @JsonProperty("description")
    private String description;

    @JsonProperty("descriptionType")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String descriptionType;


    public DataciteDescription() {
    }

    public DataciteDescription description(String description) {
        this.description = description;
        return this;
    }

    public DataciteDescription descriptionType(String descriptionType) {
        this.descriptionType = descriptionType;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionType() {
        return descriptionType;
    }

}
