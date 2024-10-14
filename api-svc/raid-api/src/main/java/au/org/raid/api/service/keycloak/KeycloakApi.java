package au.org.raid.api.service.keycloak;

import au.org.raid.api.service.keycloak.dto.AdminRaidsRequest;
import au.org.raid.api.service.keycloak.dto.TokenRequest;
import au.org.raid.api.service.keycloak.dto.TokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "keycloak", url = "${raid.iam.realm-uri}")
public interface KeycloakApi {
    @PostMapping(path = "/raid/admin-raids")
    ResponseEntity<String> addToAdminRaids(@RequestHeader(name = "Authorization") String authorization, @RequestBody AdminRaidsRequest request);

    @PostMapping(path = "protocol/openid-connect/token", consumes = "application/x-www-form-urlencoded")
    ResponseEntity<TokenResponse> getToken(@RequestBody TokenRequest request);

}
