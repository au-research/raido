package raido.apisvc.factory;

import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.Access;
import raido.idl.raidv2.model.AccessBlock;
import raido.idl.raidv2.model.AccessType;
import raido.idl.raidv2.model.AccessTypeWithSchemeUri;

import java.util.Map;

@Component
public class AccessFactory {
    private static final String ACCESS_SCHEME_URI =
        "https://github.com/au-research/raid-metadata/tree/main/scheme/access/type/v1";

    private static final Map<AccessType, String> ACCESS_TYPE_MAP = Map.of(
        AccessType.CLOSED, "https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/closed.json",
        AccessType.OPEN, "https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/open.json"
    );

    public Access create(final AccessBlock accessBlock) {
        if (accessBlock == null) {
            return null;
        }

        return new Access()
            .accessStatement(accessBlock.getAccessStatement())
            .type(new AccessTypeWithSchemeUri()
                .id(accessBlock.getType() != null ? ACCESS_TYPE_MAP.get(accessBlock.getType()) : null)
                .schemeUri(ACCESS_SCHEME_URI)
            );
    }
}