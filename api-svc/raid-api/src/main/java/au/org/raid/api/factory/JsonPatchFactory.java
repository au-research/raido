package au.org.raid.api.factory;

import jakarta.json.Json;
import jakarta.json.JsonPatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonPatchFactory {
    private final JsonValueFactory jsonValueFactory;
    public JsonPatch create(final String existing, final String updated) {
        return Json.createDiff(jsonValueFactory.create(existing).asJsonObject(), jsonValueFactory.create(updated).asJsonObject());
    }
}
