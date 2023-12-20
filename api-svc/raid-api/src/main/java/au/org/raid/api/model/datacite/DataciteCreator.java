package au.org.raid.api.model.datacite;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DataciteCreator {
    @JsonProperty("name")
    private String creator;
    private String dataciteCreator;


    public DataciteCreator(String name) {
        this.creator = name;
        this.dataciteCreator = name;
    }
}
