package au.org.raid.api.model.datacite;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DataciteContributor {

    @JsonProperty("name")
    private String contributor;

    @JsonProperty("contributorType")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String contributorType;

}
