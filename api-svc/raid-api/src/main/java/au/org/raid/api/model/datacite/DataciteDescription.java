package au.org.raid.api.model.datacite;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DataciteDescription {

    @JsonProperty("description")
    private String description;

    @JsonProperty("descriptionType")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String descriptionType;
}
