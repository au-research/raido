package au.org.raid.api.model.datacite;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DataciteData {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private DataciteDto data;

    public String toJson() throws JsonProcessingException {
        return objectMapper.writeValueAsString(this);
    }
}
