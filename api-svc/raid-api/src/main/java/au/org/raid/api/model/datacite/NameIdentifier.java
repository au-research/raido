package au.org.raid.api.model.datacite;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NameIdentifier {
    private String nameIdentifier;
    private String nameIdentifierScheme;
    private String schemeUri;
}
