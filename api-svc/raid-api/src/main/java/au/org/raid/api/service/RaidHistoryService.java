package au.org.raid.api.service;

import au.org.raid.api.entity.ChangeType;
import au.org.raid.api.factory.HandleFactory;
import au.org.raid.api.factory.JsonPatchFactory;
import au.org.raid.api.factory.JsonValueFactory;
import au.org.raid.api.factory.RaidHistoryRecordFactory;
import au.org.raid.api.repository.RaidHistoryRepository;
import au.org.raid.api.spring.RaidHistoryProperties;
import au.org.raid.db.jooq.tables.records.RaidHistoryRecord;
import au.org.raid.idl.raidv2.model.RaidCreateRequest;
import au.org.raid.idl.raidv2.model.RaidDto;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RaidHistoryService {
    public static final String EMPTY_JSON = "{}";
    private final ObjectMapper objectMapper;
    private final RaidHistoryRepository raidHistoryRepository;
    private final JsonPatchFactory jsonPatchFactory;
    private final JsonValueFactory jsonValueFactory;
    private final HandleFactory handleFactory;
    private final RaidHistoryRecordFactory raidHistoryRecordFactory;
    private final RaidHistoryProperties properties;

    @SneakyThrows
    public RaidDto save(final RaidCreateRequest request) {
        final var raidString = objectMapper.writeValueAsString(request);
        final var handle = handleFactory.create(request.getIdentifier().getId());
        final var diff = jsonPatchFactory.create(EMPTY_JSON, raidString);

        raidHistoryRepository.insert(raidHistoryRecordFactory.create(handle, 1, ChangeType.PATCH, diff));

        var raid = jsonValueFactory.create(diff);

        return objectMapper.readValue(raid.toString(), RaidDto.class);
    }

    @SneakyThrows
    public RaidDto save(final RaidUpdateRequest raid) {
        final var version = raid.getIdentifier().getVersion();
        final var newVersion = version + 1;
        raid.getIdentifier().setVersion(newVersion);

        final var raidString = objectMapper.writeValueAsString(raid);

        final var handle = handleFactory.create(raid.getIdentifier().getId());

        final var history = raidHistoryRepository.findAllByHandle(handle.toString()).stream()
                .map(RaidHistoryRecord::getDiff)
                .map(jsonValueFactory::create)
                .toList();

        final var diff = jsonPatchFactory.create(jsonValueFactory.create(history).toString(), raidString);

        raidHistoryRepository.insert(
                raidHistoryRecordFactory.create(handle, newVersion, ChangeType.PATCH, diff)
        );

        if (newVersion % properties.getBaselineInterval() == 0) {
            final var baselineDiff = jsonPatchFactory.create(EMPTY_JSON, raidString);

            raidHistoryRepository.insert(
                raidHistoryRecordFactory.create(handle, newVersion, ChangeType.BASELINE, baselineDiff)
            );
        }

        return objectMapper.readValue(jsonValueFactory.create(history, diff).toString(), RaidDto.class);
    }
}