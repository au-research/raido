package au.org.raid.api.model.datacite;

import com.fasterxml.jackson.annotation.JsonProperty;


public class DataciteCreator {

    @JsonProperty("name")
    private String name;

    public DataciteCreator() {super();}

    public DataciteCreator dataciteCreator(String name) {
        this.name = name;
        return this;
    }


    public String getDataciteCreator() {
        return name;
    }

}
