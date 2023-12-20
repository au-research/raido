package au.org.raid.api.model.datacite;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

@Data
@Accessors(chain = true)
public class DataciteIdentifier {
    @JsonProperty("identifier")
    private String identifier;

    @JsonProperty("identifierType")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String identifierType;

}
