package au.org.raid.api.model.datacite;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataciteAlternateIdentifier {

    @JsonProperty("alternateIdentifier")
    private String alternateIdentifier;

    @JsonProperty("alternateIdentifierType")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String alternateIdentifierType;


    public DataciteAlternateIdentifier() {
    }

    public DataciteAlternateIdentifier alternateIdentifier(String alternateIdentifier) {
        this.alternateIdentifier = alternateIdentifier;
        return this;
    }

    public DataciteAlternateIdentifier alternateIdentifierType(String alternateIdentifierType) {
        this.alternateIdentifierType = alternateIdentifierType;
        return this;
    }

    public String getAlternateIdentifier() {
        return alternateIdentifier;
    }

    public String getAlternateIdentifierType() {
        return alternateIdentifierType;
    }

}
