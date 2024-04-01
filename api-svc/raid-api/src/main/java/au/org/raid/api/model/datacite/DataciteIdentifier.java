package au.org.raid.api.model.datacite;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DataciteIdentifier {
    private String identifier;

    private String identifierType;

}
