package au.org.raid.api.service.datacite;

import au.org.raid.api.factory.DatacitePayloadFactory;
import au.org.raid.idl.raidv2.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class DataciteService {
    public final String createDataciteRaid(RaidCreateRequest request, String handle) {
        String responseHandle;
        try {
            URL url = new URL(System.getenv("datacite.endpoint") + "/dois");
            // RestTemplate restTemplate = new RestTemplate();
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            // Prepare the basic auth credentials
            String username = System.getenv("datacite.user");
            String password = System.getenv("datacite.password");
            String encodedCredentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

            httpURLConnection.setRequestProperty("Authorization", "Basic " + encodedCredentials);

            DatacitePayloadFactory payloadFactory = new DatacitePayloadFactory();
            String createPayload = payloadFactory.payloadForCreate(request, handle);


            try (OutputStream os = httpURLConnection.getOutputStream()) {
                byte[] input = createPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (InputStream inputStream = httpURLConnection.getInputStream()) {
                ObjectMapper inputStreamObjectMapper = new ObjectMapper();
                JsonNode inputStreamRootNode = inputStreamObjectMapper.readTree(inputStream);
                responseHandle = inputStreamRootNode.path("data").path("id").asText();
            } catch (IOException e) {
                throw new RuntimeException("Error reading response");
            }

        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException("Error creating handle");
        }

        return responseHandle;
    }

    public final String getDatacitePrefix(){
        return System.getenv("datacite.handle.prefix");
    }

    public final String getDataciteSuffix(){
        // TODO: This is a temporary solution to create a random identifier for the datacite handle
        // This should be replaced with a proper solution
        return UUID.randomUUID().toString().split("-")[0];
    }

    public final void updateDataciteRaid(RaidUpdateRequest request, String handle) {
        try {
            URL url = new URL(System.getenv("datacite.endpoint") + "/dois");

            // RestTemplate restTemplate = new RestTemplate();
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("PUT");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            // Prepare the basic auth credentials
            String username = System.getenv("datacite.user");
            String password = System.getenv("datacite.password");
            String encodedCredentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

            httpURLConnection.setRequestProperty("Authorization", "Basic " + encodedCredentials);

            DatacitePayloadFactory payloadFactory = new DatacitePayloadFactory();
            String payloadForUpdate = payloadFactory.payloadForUpdate(request, handle);


            try (OutputStream os = httpURLConnection.getOutputStream()) {
                byte[] input = payloadForUpdate.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (InputStream inputStream = httpURLConnection.getInputStream()) {
                ObjectMapper inputStreamObjectMapper = new ObjectMapper();
                JsonNode inputStreamRootNode = inputStreamObjectMapper.readTree(inputStream);
            } catch (IOException e) {
                System.out.println(e);
                throw new RuntimeException("Error reading response");
            }

        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException("Error updating handle");
        }
    }
}
