package au.org.raid.api.service.doihandle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class DoiSessionAuthenticator {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String TYPE_VALUE = "HS_PUBKEY";
    private static final String ALG_VALUE = "SHA256";
    private static final String ID_VALUE = System.getenv("DOI_ID");

    public boolean authenticateSession(String sessionId, String signatureEncoded, String cnonceEncoded)
            throws Exception {

        String handleServer = System.getenv("DOI_HANDLE_SERVER");
        URL url = new URL(new URI(handleServer).resolve("/api/sessions/this").toString());

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        ObjectNode requestBody = OBJECT_MAPPER.createObjectNode();
        requestBody.put("sessionId", sessionId);
        requestBody.put("id", ID_VALUE);
        requestBody.put("type", TYPE_VALUE);
        requestBody.put("cnonce", cnonceEncoded);
        requestBody.put("alg", ALG_VALUE);
        requestBody.put("signature", signatureEncoded);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestBody.toString().getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("Error authenticating session");
        }

        JsonNode responseNode = OBJECT_MAPPER.readTree(connection.getInputStream());

        if (responseNode.hasNonNull("authenticated")) {
            return responseNode.get("authenticated").asBoolean();
        } else {
            throw new RuntimeException("The 'authenticated' field is missing or null in the response.");
        }
    }
}
