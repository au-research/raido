package au.org.raid.api.controller;

import au.org.raid.api.dto.ServicePointDto.ServicePointDto;
import au.org.raid.api.dto.UserDto;
import au.org.raid.api.service.ServicePointService;
import au.org.raid.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Validated
@RequestMapping("/service-point")
@RestController
@RequiredArgsConstructor
public class ServicePointController {

    private final ServicePointService servicePointService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ServicePointDto> create(
            @RequestBody final ServicePointDto servicePoint
    ) {

        final var created = servicePointService.create(servicePoint);

        return ResponseEntity
                .created(URI.create("/service-point/%d".formatted(created.getId())))
                .body(created);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<ServicePointDto> update(
            @PathVariable final Long id,
            @RequestBody final ServicePointDto servicePoint
    ) {
        return ResponseEntity.ok(servicePointService.update(servicePoint));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ServicePointDto> findById(
            @PathVariable final Long id
    ) {
        return ResponseEntity.of(servicePointService.findById(id));
    }
    @GetMapping(path = "/{id}/user/")
    public ResponseEntity<List<UserDto>> findUsersByServicePointId(
            @PathVariable final Long id
    ) {
        return ResponseEntity.ok(userService.findAllByServicePointId(id));
    }
}
