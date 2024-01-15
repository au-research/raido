package au.org.raid.api.model.datacite;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DataciteDate {

    @JsonProperty("date")
    private String date;

    @JsonProperty("dateType")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dateType;
}
