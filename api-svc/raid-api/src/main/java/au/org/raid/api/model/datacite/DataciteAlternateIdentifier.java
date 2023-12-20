package au.org.raid.api.model.datacite;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DataciteAlternateIdentifier {

    @JsonProperty("alternateIdentifier")
    private final String alternateIdentifier;

    @JsonProperty("alternateIdentifierType")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String alternateIdentifierType;

}