package au.org.raid.api.model.datacite;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataciteData {
    private DataciteDto data;

    public DataciteData() {
        super();
    }


    public DataciteData setData(DataciteDto dataciteDto) {
        this.data = dataciteDto;
        return this;
    }

    @JsonProperty("data")
    public DataciteDto getData() {
        return this.data;
    }


    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
