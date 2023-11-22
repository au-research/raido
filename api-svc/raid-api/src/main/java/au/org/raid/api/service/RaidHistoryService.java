package au.org.raid.api.service;

import au.org.raid.api.entity.ChangeType;
import au.org.raid.api.factory.JsonObjectFactory;
import au.org.raid.api.factory.JsonPatchFactory;
import au.org.raid.api.repository.RaidHistoryRepository;
import au.org.raid.db.jooq.tables.records.RaidHistoryRecord;
import au.org.raid.idl.raidv2.model.RaidCreateRequest;
import au.org.raid.idl.raidv2.model.RaidDto;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.Json;
import jakarta.json.JsonPatch;
import jakarta.json.JsonValue;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RaidHistoryService {
    public static final String EMPTY_JSON = "{}";
    private final ObjectMapper objectMapper;
    private final RaidHistoryRepository raidHistoryRepository;
    private final JsonPatchFactory jsonPatchFactory;
    private final JsonObjectFactory jsonObjectFactory;
    private final int baselineInterval = 5;

    @SneakyThrows
    public RaidDto save(final RaidCreateRequest raid) {
        final var raidString = objectMapper.writeValueAsString(raid);
        final var handle = new Handle(raid.getIdentifier().getId());
        final var diff = jsonPatchFactory.create(EMPTY_JSON, raidString);

        raidHistoryRepository.insert(new RaidHistoryRecord()
                .setHandle(handle.toString())
                .setRevision(1)
                .setChangeType(ChangeType.PATCH.toString())
                .setDiff(diff.toString()));

        var json = Json.createReader(new StringReader(EMPTY_JSON)).readValue().asJsonObject();
        json = diff.apply(json);

        return objectMapper.readValue(json.toString(), RaidDto.class);
    }

    @SneakyThrows
    public RaidDto save(final RaidUpdateRequest raid) {
        final var version = raid.getIdentifier().getVersion();
        final var newVersion = version + 1;
        raid.getIdentifier().setVersion(newVersion);

        final var raidString = objectMapper.writeValueAsString(raid);

        final var handle = new Handle(raid.getIdentifier().getId());

        final var history = new ArrayList<>(raidHistoryRepository.findAllByHandle(handle.toString()));

        final var diff = jsonPatchFactory.create(build(history).toString(), raidString);

        raidHistoryRepository.insert(new RaidHistoryRecord()
                .setHandle(handle.toString())
                .setRevision(newVersion)
                .setChangeType(ChangeType.PATCH.toString())
                .setDiff(diff.toString()));

        if (newVersion % baselineInterval == 0) {
            final var baselineDiff = jsonPatchFactory.create(EMPTY_JSON, raidString);

            raidHistoryRepository.insert(new RaidHistoryRecord()
                    .setHandle(handle.toString())
                    .setRevision(newVersion)
                    .setChangeType(ChangeType.BASELINE.toString())
                    .setDiff(baselineDiff.toString()));
        }

        return objectMapper.readValue(build(history, diff).toString(), RaidDto.class);
    }

    private JsonValue build(final List<RaidHistoryRecord> history, final JsonPatch patch) {
        final var json = build(history);

        return patch.apply(json.asJsonObject());
    }

    @SneakyThrows
    private JsonValue build(final List<RaidHistoryRecord> history) {
        var json = jsonObjectFactory.create(EMPTY_JSON);

        for (final var item : history) {
            final var change = Json.createReader(new StringReader(item.getDiff())).readArray();
            final var patch = Json.createPatch(change);
            json = patch.apply(json);
        }

        return json;
    }

}
