package au.org.raid.api.controller;

import au.org.raid.api.dto.UserDto;
import au.org.raid.api.service.ServicePointService;
import au.org.raid.api.service.UserService;
import au.org.raid.idl.raidv2.api.ServicePointApi;
import au.org.raid.idl.raidv2.model.ServicePointCreateRequest;
import au.org.raid.idl.raidv2.model.ServicePoint;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ServicePointController implements ServicePointApi {
    private final ServicePointService servicePointService;
    private final UserService userService;

    public ResponseEntity<List<ServicePoint>> findAllServicePoints() {
        return ResponseEntity.ok(servicePointService.findAll());
    }

    public ResponseEntity<ServicePoint> createServicePoint(
            final ServicePointCreateRequest servicePoint
    ) {

        final var created = servicePointService.create(servicePoint);

        return ResponseEntity
                .created(URI.create("/service-point/%d".formatted(created.getId())))
                .body(created);
    }

    public ResponseEntity<ServicePoint> updateServicePoint(
            final Long id,
            final ServicePoint servicePoint
    ) {
        return ResponseEntity.ok(servicePointService.update(servicePoint));
    }

    public ResponseEntity<ServicePoint> findServicePointById(final Long id) {
        return ResponseEntity.of(servicePointService.findById(id));
    }

    public ResponseEntity<List<UserDto>> findUsersByServicePointId(final Long id) {
        return ResponseEntity.ok(userService.findAllByServicePointId(id));
    }
}
