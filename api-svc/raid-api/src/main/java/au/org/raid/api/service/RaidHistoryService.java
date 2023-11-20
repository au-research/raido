package au.org.raid.api.service;

import au.org.raid.api.entity.ChangeType;
import au.org.raid.api.repository.RaidHistoryRepository;
import au.org.raid.db.jooq.tables.records.RaidHistoryRecord;
import au.org.raid.idl.raidv2.model.RaidCreateRequest;
import au.org.raid.idl.raidv2.model.RaidDto;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.Json;
import jakarta.json.JsonMergePatch;
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
    private final ObjectMapper objectMapper;
    private final RaidHistoryRepository raidHistoryRepository;

    @SneakyThrows
    public RaidDto save(final RaidCreateRequest raid) {
        final var raidString = objectMapper.writeValueAsString(raid);
        final var handle = new Handle(raid.getIdentifier().getId());
        final var diff = createDiff("{}", raidString);

        raidHistoryRepository.insert(new RaidHistoryRecord()
                .setHandle(handle.toString())
                .setRevision(1)
                .setChangeType(ChangeType.PATCH.toString())
                .setDiff(diff.toString()));

        var json = Json.createReader(new StringReader("{}")).readValue();
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

        final var diff = createDiff(build(history).toString(), raidString);

        raidHistoryRepository.insert(new RaidHistoryRecord()
                .setHandle(handle.toString())
                .setRevision(newVersion)
                .setChangeType(ChangeType.PATCH.toString())
                .setDiff(diff.toString()));

        return objectMapper.readValue(build(history, diff).toString(), RaidDto.class);
    }

    private JsonMergePatch createDiff(final String existing, final String updated) {
        final var existingJson = Json.createReader(new StringReader(existing)).readValue();
        final var updatedJson = Json.createReader(new StringReader(updated)).readValue();

        return Json.createMergeDiff(existingJson, updatedJson);
    }

    private JsonValue build(final List<RaidHistoryRecord> history, final JsonMergePatch patch) {
        final var json = build(history);

        return patch.apply(json);
    }

    @SneakyThrows
    private JsonValue build(final List<RaidHistoryRecord> history) {
        var json = Json.createReader(new StringReader("{}")).readValue();

        for (final var item : history) {
            final var change = Json.createReader(new StringReader(item.getDiff())).readValue();
            final var patch = Json.createMergePatch(change);
            json = patch.apply(json);
        }

        return json;
    }

}
