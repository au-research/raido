package au.org.raid.api.factory;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.springframework.stereotype.Component;

import java.io.StringReader;

@Component
public class JsonObjectFactory {
    public JsonObject create(final String json) {
        return Json.createReader(new StringReader(json)).readObject();
    }
}
