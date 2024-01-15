package au.org.raid.api.model.datacite;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataciteRequest {
    private DataciteDto data;
}
