package au.org.raid.api.service;

import au.org.raid.api.config.properties.RaidHistoryProperties;
import au.org.raid.api.entity.ChangeType;
import au.org.raid.api.exception.InvalidVersionException;
import au.org.raid.api.factory.*;
import au.org.raid.api.repository.RaidHistoryRepository;
import au.org.raid.db.jooq.tables.records.RaidHistoryRecord;
import au.org.raid.idl.raidv2.model.RaidChange;
import au.org.raid.idl.raidv2.model.RaidCreateRequest;
import au.org.raid.idl.raidv2.model.RaidDto;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    private final RaidChangeFactory raidChangeFactory;

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

        final var recordsUpdated = raidHistoryRepository.insert(
                raidHistoryRecordFactory.create(handle, newVersion, ChangeType.PATCH, diff)
        );

        if (recordsUpdated < 1) {
            throw new InvalidVersionException(version);
        }

        if (newVersion % properties.getBaselineInterval() == 0) {
            final var baselineDiff = jsonPatchFactory.create(EMPTY_JSON, raidString);

            raidHistoryRepository.insert(
                raidHistoryRecordFactory.create(handle, newVersion, ChangeType.BASELINE, baselineDiff)
            );
        }

        return objectMapper.readValue(jsonValueFactory.create(history, diff).toString(), RaidDto.class);
    }

    @SneakyThrows
    public Optional<RaidDto> findByHandleAndVersion(final String handle, final Integer version) {
        final var history = raidHistoryRepository.findAllByHandleAndVersion(handle, version).stream()
                .map(RaidHistoryRecord::getDiff)
                .map(jsonValueFactory::create)
                .toList();

        if (history.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(objectMapper.readValue(jsonValueFactory.create(history).toString(), RaidDto.class));
     }

    @SneakyThrows
    public Optional<RaidDto> findByHandle(final String handle) {
        final var history = raidHistoryRepository.findAllByHandle(handle).stream()
                .map(RaidHistoryRecord::getDiff)
                .map(jsonValueFactory::create)
                .toList();

        if (history.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(objectMapper.readValue(jsonValueFactory.create(history).toString(), RaidDto.class));
    }

    public List<RaidChange> findAllChangesByHandle(final String handle) {
        return raidHistoryRepository.findAllByHandleAndChangeType(handle, ChangeType.PATCH.toString()).stream()
                .map(raidChangeFactory::create)
                .toList();
    }
}
