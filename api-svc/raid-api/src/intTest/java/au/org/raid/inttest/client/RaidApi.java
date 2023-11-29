package au.org.raid.inttest.client;

import au.org.raid.db.jooq.enums.UserRole;
import au.org.raid.idl.raidv2.model.RaidCreateRequest;
import au.org.raid.idl.raidv2.model.RaidDto;
import au.org.raid.inttest.auth.BootstrapAuthTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class RaidApi {
    private final BootstrapAuthTokenService tokenService;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    public RaidDto create(final long servicePointId, final UserRole userRole, final RaidCreateRequest raid) {
        final var subject = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        // create token
        final var token = tokenService.bootstrapToken(servicePointId, subject, userRole);

        RequestEntity<RaidCreateRequest> request = RequestEntity
                .put("http://localhost:10001/raid")
                .header("Authorization", "Bearer %s".formatted(token))
                .body(raid);

        final var response = restTemplate.exchange(request, RaidDto.class);

        return response.getBody();
    }

}
