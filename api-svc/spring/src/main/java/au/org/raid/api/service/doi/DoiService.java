package au.org.raid.api.service.doi;

import au.org.raid.api.validator.AbstractUriValidator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;

@Getter
@RequiredArgsConstructor
public class DoiService extends AbstractUriValidator {
    public final String regex = "^http[s]?://doi\\.org/10\\..*";
    private final RestTemplate restTemplate;
}
