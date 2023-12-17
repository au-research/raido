package au.org.raid.api.model.datacite;

import com.fasterxml.jackson.annotation.JsonProperty;


public class DataciteCreator {

    private String name;

    public DataciteCreator() {super();}

    public DataciteCreator(String name) {
        this.name = name;
    }

    public DataciteCreator dataciteCreator(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("name")
    public String getDataciteCreator() {
        return name;
    }

}
