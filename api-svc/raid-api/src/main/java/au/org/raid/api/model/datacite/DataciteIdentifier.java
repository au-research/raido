package au.org.raid.api.model.datacite;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class DataciteIdentifier {

    private String identifier;
    private String identifierType;

    public DataciteIdentifier() {
    }

    public DataciteIdentifier(String identifier, String identifierType) {
        this.identifier = identifier;
        this.identifierType = identifierType;
    }

}
