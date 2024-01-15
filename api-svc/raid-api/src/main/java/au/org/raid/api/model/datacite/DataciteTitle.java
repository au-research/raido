package au.org.raid.api.model.datacite;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DataciteTitle {

    @JsonProperty("title")
    private String dataciteTitle;

    @JsonProperty("titleType")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String titleType;
}
