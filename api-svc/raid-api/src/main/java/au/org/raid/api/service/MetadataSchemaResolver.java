package au.org.raid.api.service;

import au.org.raid.db.jooq.tables.records.RaidRecord;
import au.org.raid.idl.raidv2.model.MetadataSchema;
import au.org.raid.idl.raidv2.model.RaidDto;
import au.org.raid.idl.raidv2.model.RaidDtoV005;
import au.org.raid.idl.raidv2.model.RaidDtoV100;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class MetadataSchemaResolver {
    private final ObjectMapper objectMapper;
    private final RaidHistoryService raidHistoryService;
    private final CacheableRaidService cacheableRaidService;

    private final Map<String, Function<RaidRecord, MetadataSchema>> resolverMap = Map.of(
            "0.0.5", new Function<>() {
                @Override
                @SneakyThrows
                public MetadataSchema apply(final RaidRecord record) {
                    final var raid =  objectMapper.readValue(record.getMetadata().data(), RaidDtoV005.class);
                    raid.setSchemaVersion("0.0.5");
                    return raid;
                }
            },
            "1.0.0", new Function<>() {
                @SneakyThrows
                @Override
                public MetadataSchema apply(final RaidRecord record) {
                    if (record.getHandle().startsWith("10378.1")) {
                        return objectMapper.readValue(record.getMetadata().data(), RaidDtoV100.class);
                    }

                    final var raid = raidHistoryService.findByHandle(record.getHandle())
                            .orElseGet(() -> cacheableRaidService.build(record));

                    if (raid != null) {
                        ((RaidDtoV100) raid).setSchemaVersion("1.0.0");
                    }

                    return raid;
                }
            },
            "2.0.0", new Function<>() {
                @SneakyThrows
                @Override
                public MetadataSchema apply(final RaidRecord record) {
                    final var raid = raidHistoryService.findByHandle(record.getHandle())
                            .orElseGet(() -> cacheableRaidService.build(record));

                    if (raid != null) {
                        ((RaidDto) raid).setSchemaVersion("2.0.0");
                    }

                    return raid;
                }
            }
    );

    public MetadataSchema resolve(final RaidRecord record) {
        return resolverMap.get(record.getSchemaVersion()).apply(record);
    }
}
