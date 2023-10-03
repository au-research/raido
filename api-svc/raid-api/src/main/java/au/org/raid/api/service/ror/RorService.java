package au.org.raid.api.service.ror;

import au.org.raid.api.validator.AbstractUriValidator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;

@Getter
@RequiredArgsConstructor
public class RorService extends AbstractUriValidator {
    // see https://ror.readme.io/docs/ror-identifier-pattern
    private final String regex = "^https://ror\\.org/[0-9a-z]{9}$";
    private final RestTemplate restTemplate;
}
