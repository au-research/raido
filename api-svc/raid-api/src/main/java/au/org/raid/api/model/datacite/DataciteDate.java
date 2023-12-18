package au.org.raid.api.model.datacite;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataciteDate {

    @JsonProperty("date")
    private String date;

    @JsonProperty("dateType")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dateType;


    public DataciteDate() {
    }

    public DataciteDate date(String date) {
        this.date = date;
        return this;
    }

    public DataciteDate dateType(String dateType) {
        this.dateType = dateType;
        return this;
    }

    public String getDate() {
        return date;
    }

    public String getDateType() {
        return dateType;
    }

}
