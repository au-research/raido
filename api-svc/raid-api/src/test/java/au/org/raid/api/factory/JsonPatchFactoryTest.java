package au.org.raid.api.factory;

import jakarta.json.JsonNumber;
import jakarta.json.JsonString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
class JsonPatchFactoryTest {
    private final JsonPatchFactory jsonPatchFactory = new JsonPatchFactory(new JsonValueFactory());

    @Test
    @DisplayName("Returns new JsonPatch")
    void create() {
        final var existing = "{\"abc\": 123}";
        final var updated = "{\"abc\": 456}";

        final var diff = jsonPatchFactory.create(existing, updated).toJsonArray();
        final var jsonObject = diff.getJsonObject(0);

        assertThat(diff.size(), is(1));
        assertThat(((JsonString) jsonObject.get("op")).getString(), is("replace"));
        assertThat(((JsonString) jsonObject.get("path")).getString(), is("/abc"));
        assertThat(((JsonNumber) jsonObject.get("value")).intValue(), is(456));
    }
}