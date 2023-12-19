package au.org.raid.api.model.datacite;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataciteContributor {

    @JsonProperty("name")
    private String name;

    @JsonProperty("contributorType")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String contributorType;


    public DataciteContributor() {
    }

    public DataciteContributor dataciteContributor(String name) {
        this.name = name;
        return this;
    }

    public DataciteContributor contributorType(String contributorType) {
        this.contributorType = contributorType;
        return this;
    }

    public String getContributor() {
        return name;
    }

    public String getContributorType() {
        return contributorType;
    }

}
