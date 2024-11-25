package au.org.raid.api.controller;

import au.org.raid.api.service.RaidIngestService;
import au.org.raid.idl.raidv2.api.ContributorApi;
import au.org.raid.idl.raidv2.model.RaidDto;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@SecurityScheme(name = "bearerAuth", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class ContributorController implements ContributorApi {
    private final RaidIngestService raidIngestService;

    @Override
    public ResponseEntity<List<RaidDto>> findAllById(final String id) {
        final var raids = raidIngestService.findAllByContributor(id);

        return ResponseEntity.ok(raids);
    }
}
