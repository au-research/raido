package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AccessFactory {
    private static final String ACCESS_SCHEME_URI =
            "https://github.com/au-research/raid-metadata/tree/main/scheme/access/type/v1/";

    private static final Map<AccessType, String> ACCESS_TYPE_MAP = Map.of(
            AccessType.CLOSED, "https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/closed.json",
            AccessType.OPEN, "https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/open.json"
    );

    public Access create(final AccessBlock accessBlock) {
        if (accessBlock == null) {
            return null;
        }

        AccessStatement accessStatement = null;
        if (accessBlock.getType() != null && accessBlock.getType() != AccessType.OPEN) {
            accessStatement = new AccessStatement().statement(accessBlock.getAccessStatement());
        }

        return new Access()
                .accessStatement(accessStatement)
                .type(new AccessTypeWithSchemaUri()
                        .id(accessBlock.getType() != null ? ACCESS_TYPE_MAP.get(accessBlock.getType()) : null)
                        .schemaUri(ACCESS_SCHEME_URI)
                );
    }
}