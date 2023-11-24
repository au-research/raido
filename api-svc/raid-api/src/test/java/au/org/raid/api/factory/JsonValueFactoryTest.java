package au.org.raid.api.factory;

import jakarta.json.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class JsonValueFactoryTest {
    private final JsonValueFactory jsonValueFactory = new JsonValueFactory();

    @Test
    @DisplayName("Create new JsonValue with history returns current raid")
    void createWithHistory() {
        final var history = new ArrayList<JsonValue>();

        final var existing = Json.createReader(new StringReader("{}")).readObject();
        final var update1 = Json.createReader(new StringReader("{\"abc\":123}")).readObject();
        history.add(Json.createDiff(existing, update1).toJsonArray());

        var json = jsonValueFactory.create(history).asJsonObject();
        assertThat(((JsonNumber) json.get("abc")).intValue(), is(123));

        final var update2 = Json.createReader(new StringReader("{\"abc\":234}")).readObject();
        history.add(Json.createDiff(update1, update2).toJsonArray());

        json = jsonValueFactory.create(history).asJsonObject();
        assertThat(((JsonNumber) json.get("abc")).intValue(), is(234));

        final var update3 = Json.createReader(new StringReader("{\"abc\":456}")).readObject();
        history.add(Json.createDiff(update2, update3).toJsonArray());

        json = jsonValueFactory.create(history).asJsonObject();
        assertThat(((JsonNumber) json.get("abc")).intValue(), is(456));
    }

    @Test
    @DisplayName("Create new JsonValue with history and JsonPatch returns current raid")
    void createWithHistoryAnJsonPatch() {
        final var history = new ArrayList<JsonValue>();

        final var existing = Json.createReader(new StringReader("{}")).readObject();
        final var update1 = Json.createReader(new StringReader("{\"abc\":123}")).readObject();
        history.add(Json.createDiff(existing, update1).toJsonArray());
        final var update2 = Json.createReader(new StringReader("{\"abc\":234}")).readObject();
        history.add(Json.createDiff(update1, update2).toJsonArray());
        final var update3 = Json.createReader(new StringReader("{\"abc\":456}")).readObject();
        final var patch = Json.createDiff(update2, update3);

        final var json = jsonValueFactory.create(history, patch).asJsonObject();
        assertThat(((JsonNumber) json.get("abc")).intValue(), is(456));
    }

    @Test
    @DisplayName("Create new JsonValue with json string")
    void createWithString() {
        final var input = "{\"abc\":123}";
        final var json = jsonValueFactory.create(input).asJsonObject();
        assertThat(((JsonNumber) json.get("abc")).intValue(), is(123));
    }

    @Test
    @DisplayName("Create new JsonValue from JsonPatch")
    void createWithJsonPatch() {
        final var existing = Json.createReader(new StringReader("{}")).readObject();
        final var updated = Json.createReader(new StringReader("{\"abc\":123}")).readObject();
        final var patch = Json.createDiff(existing, updated);
        final var json = jsonValueFactory.create(patch).asJsonObject();

        assertThat(((JsonNumber) json.get("abc")).intValue(), is(123));
    }
}