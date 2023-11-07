package au.org.raid.api.service.doi;

import au.org.raid.api.validator.AbstractUriValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Getter
@RequiredArgsConstructor
public class DoiService extends AbstractUriValidator {
    public final String regex = "^http[s]?://doi\\.org/10\\..*";
    private final RestTemplate restTemplate;
}

