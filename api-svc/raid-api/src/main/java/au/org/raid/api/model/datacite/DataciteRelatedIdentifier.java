package au.org.raid.api.model.datacite;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DataciteRelatedIdentifier {

    @JsonProperty("relatedIdentifier")
    private String relatedIdentifier;

    @JsonProperty("relatedIdentifierType")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String relatedIdentifierType;

    @JsonProperty("relationType")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String relationType;
}
