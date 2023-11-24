package au.org.raid.api.factory;

import jakarta.json.Json;
import jakarta.json.JsonPatch;
import jakarta.json.JsonValue;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JsonValueFactory {
    public static final String EMPTY_JSON = "{}";
    @SneakyThrows
    public JsonValue create(final List<JsonValue> history) {
        var json = this.create(EMPTY_JSON);

        for (final var item : history) {
            final var change = item.asJsonArray();
            final var patch = Json.createPatch(change);
            json = patch.apply(json.asJsonObject());
        }

        return json;
    }

    public JsonValue create(final List<JsonValue> history, final JsonPatch patch) {
        final var json = this.create(history);

        return patch.apply(json.asJsonObject());
    }

    public JsonValue create(final JsonPatch diff) {
        var json = Json.createReader(new StringReader(EMPTY_JSON)).readObject();
        return diff.apply(json);
    }

    public JsonValue create(final String json) {
        return Json.createReader(new StringReader(json)).readValue();
    }
}
