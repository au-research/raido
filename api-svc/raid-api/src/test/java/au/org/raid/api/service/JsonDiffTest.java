package au.org.raid.api.service;


import org.junit.jupiter.api.Test;

import jakarta.json.Json;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class JsonDiffTest {


    @Test
    void name() throws URISyntaxException, IOException {
        var closedRaid = Files.readString(Paths.get(Objects.requireNonNull(getClass().getResource("/fixtures/raid.json")).toURI()));
        var embargoedRaid = Files.readString(Paths.get(Objects.requireNonNull(getClass().getResource("/fixtures/embargoed-raid.json")).toURI()));


//        var closed = Json.createReader(new StringReader(closedRaid)).readObject();
        var closed = Json.createReader(new StringReader("{}")).readObject();
        var embargoed = Json.createReader(new StringReader(embargoedRaid)).readObject();


        final var diff = Json.createDiff(closed, embargoed);

        final var diffArray = Json.createReader(new StringReader(diff.toString())).readArray();

        final var patch = Json.createPatch(diffArray);
        patch.apply(closed);


    }
}
