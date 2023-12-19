package au.org.raid.api.model.datacite;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataciteRelatedIdentifier {

    @JsonProperty("relatedIdentifier")
    private String relatedIdentifier;

    @JsonProperty("relatedIdentifierType")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String relatedIdentifierType;

    @JsonProperty("relationType")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String relationType;


    public DataciteRelatedIdentifier() {
    }

    public DataciteRelatedIdentifier relatedIdentifier(String relatedIdentifier) {
        this.relatedIdentifier = relatedIdentifier;
        return this;
    }

    public DataciteRelatedIdentifier relatedIdentifierType(String relatedIdentifierType) {
        this.relatedIdentifierType = relatedIdentifierType;
        return this;
    }

    public String getRelatedIdentifier() {
        return relatedIdentifier;
    }

    public String getRelatedIdentifierType() {
        return relatedIdentifierType;
    }

    public String getRelationType() {
        return relationType;
    }

}
