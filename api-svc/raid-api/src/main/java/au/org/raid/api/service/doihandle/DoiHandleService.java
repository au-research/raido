package au.org.raid.api.service.doihandle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DoiHandleService {

    private final String createDoiPayload(String raidMetaDataStringified, String suffix) {
        ObjectMapper handleObjectMapper = new ObjectMapper();
        ObjectNode handleRootNode = handleObjectMapper.createObjectNode();

        String handle = System.getenv("DOI_HANDLE_PREFIX") + "/" + suffix;
        handleRootNode.put("handle", handle);
        ArrayNode valuesArray = handleRootNode.putArray("values");

        ObjectNode valueNode = valuesArray.addObject();
        valueNode.put("index", 100);
        valueNode.put("type", "HS_ADMIN");

        ObjectNode dataNode = valueNode.putObject("data");
        dataNode.put("format", "admin");

        ObjectNode valueDataNode = dataNode.putObject("value");
        valueDataNode.put("handle", "0.NA/10.5555.25");
        valueDataNode.put("index", 200);
        valueDataNode.put("permissions", "1110");
        valueDataNode.put("format", "admin");

        ObjectNode descNode = valuesArray.addObject();
        descNode.put("index", 1);
        descNode.put("type", "DESC");

        ObjectNode metadataNode = descNode.putObject("data");
        metadataNode.put("format", "string");
        metadataNode.put("value", raidMetaDataStringified);

        return handleRootNode.toString();
    }

    public String createDoi(String sessionId, String raidMetaDataStringified, String suffix) {
        String doiHandle;

        String prefix = System.getenv("DOI_HANDLE_PREFIX");
        String handle = prefix + "/" + suffix;

        try {
            URL url = new URL(System.getenv("DOI_HANDLE_SERVER") + "/api/handles/" + handle);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("PUT");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Authorization", "Handle sessionId=\"" + sessionId + "\"");

            String reservePayload = createDoiPayload(raidMetaDataStringified, suffix);

            try (OutputStream os = httpURLConnection.getOutputStream()) {
                byte[] input = reservePayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (InputStream inputStream = httpURLConnection.getInputStream()) {
                ObjectMapper inputStreamObjectMapper = new ObjectMapper();
                JsonNode inputStreamRootNode = inputStreamObjectMapper.readTree(inputStream);
                doiHandle = inputStreamRootNode.path("handle").asText();
            } catch (IOException e) {
                throw new RuntimeException("Error reading response");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error creating handle");
        }

        if (doiHandle.isEmpty()) {
            throw new RuntimeException("Error creating handle");
        }

        return doiHandle;
    }

}
