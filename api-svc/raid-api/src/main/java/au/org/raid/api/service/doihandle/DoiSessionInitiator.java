package au.org.raid.api.service.doihandle;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

class DoiSessionResponse {
    public String sessionId;
    public String nonce;
}

class DoiSessionInitiatorResponse {
    public String sessionId;
    public String nonceEncoded;
}

@Service
public class DoiSessionInitiator {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public DoiSessionInitiatorResponse initiate(String handleServer) {
        DoiSessionInitiatorResponse result = new DoiSessionInitiatorResponse();
        try {
            URL url = new URL(new URI(handleServer).resolve("/api/sessions").toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            int status = con.getResponseCode();

            if (status != 201) {
                throw new RuntimeException("Error initiating session");
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                DoiSessionResponse doiSessionResponse = OBJECT_MAPPER.readValue(content.toString(), DoiSessionResponse.class);

                result.sessionId = doiSessionResponse.sessionId;
                result.nonceEncoded = doiSessionResponse.nonce;
            }

            con.disconnect();
        } catch (Exception e) {
            System.err.println("Error initiating session: " + e.getMessage());
        }

        return result;
    }
}
