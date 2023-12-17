package au.org.raid.api.model.datacite;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

public class DataciteTitle {

    @JsonProperty("title")
    private String dataciteTitle;

    @JsonProperty("titleType")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String titleType;


    public DataciteTitle() {
    }

    public DataciteTitle dataciteTitle(String dataciteTitle) {
        this.dataciteTitle = dataciteTitle;
        return this;
    }

    public DataciteTitle titleType(String titleType) {
        this.titleType = titleType;
        return this;
    }

    public String getDataciteTitle() {
        return dataciteTitle;
    }

    public String getTitleType() {
        return titleType;
    }

}
