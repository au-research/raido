package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteDto;
import au.org.raid.idl.raidv2.model.RaidCreateRequest;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataciteDtoFactory {
    private static final String TYPE = "dois";
    private static final String SCHEMA_VERSION = "http://datacite.org/schema/kernel-4";
    private static final String IDENTIFIER_TYPE = "DOI";

    private final DataciteAttributesDtoFactory dataciteAttributesDtoFactory;
    private final DataciteIdentifierFactory dataciteIdentifierFactory;

    @SneakyThrows
    public DataciteDto create(RaidCreateRequest request, String handle) {
        return new DataciteDto()
                .setSchemaVersion(SCHEMA_VERSION)
                .setType(TYPE)
                .setAttributes(dataciteAttributesDtoFactory.create(request, handle))
                .setIdentifier(dataciteIdentifierFactory.create(handle, IDENTIFIER_TYPE));
    }

    @SneakyThrows
    public DataciteDto create(RaidUpdateRequest request, String handle) {
        return new DataciteDto()
                .setSchemaVersion(SCHEMA_VERSION)
                .setType(TYPE)
                .setAttributes(dataciteAttributesDtoFactory.create(request, handle))
                .setIdentifier(dataciteIdentifierFactory.create(handle, IDENTIFIER_TYPE));
    }
}
