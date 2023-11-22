package au.org.raid.api.factory;

import jakarta.json.Json;
import jakarta.json.JsonPatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonPatchFactory {
    private final JsonObjectFactory jsonObjectFactory;
    public JsonPatch create(final String existing, final String updated) {
        return Json.createDiff(jsonObjectFactory.create(existing), jsonObjectFactory.create(updated));
    }
}
