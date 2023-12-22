package au.org.raid.api.controller;

import au.org.raid.api.dto.TeamDto;
import au.org.raid.api.dto.TeamUserDto;
import au.org.raid.api.dto.UserDto;
import au.org.raid.api.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @PostMapping(path = "/service-point/{servicePointId}/team/")
    public ResponseEntity<TeamDto> create(
            @PathVariable final Long servicePointId,
            @RequestBody final TeamDto teamDto) {
        final var saved = teamService.create(teamDto, servicePointId);

        final var uri = "/service-point/%d/team/%s".formatted(servicePointId, teamDto.getId());

        return ResponseEntity.created(URI.create(uri)).body(saved);
    }

    @GetMapping(path = "/service-point/{servicePointId}/team/")
    public ResponseEntity<List<TeamDto>> findAllByServicePointId(
            @PathVariable final Long servicePointId) {
        final var teams = teamService.findAllByServicePointId(servicePointId);

        return ResponseEntity.ok(teams);
    }

    @GetMapping(path = "/team/{id}")
    public ResponseEntity<TeamDto> findById(
            @PathVariable final String id
    ) {
        return ResponseEntity.of(teamService.findById(id));
    }

    @PutMapping(path = "/team/{id}")
    public ResponseEntity<TeamDto> updateByIdAndServicePointId(
            @PathVariable final Long servicePointId,
            @PathVariable final String id,
            @RequestBody final TeamDto teamDto
    ) {
        final var saved = teamService.updateByIdAndServicePointId(id, servicePointId, teamDto);

        return ResponseEntity.of(saved);
    }

    @PostMapping(path = "/team/{teamId}/user/")
    public ResponseEntity<List<TeamUserDto>> addUserToTeam(
            @PathVariable final String teamId,
            @RequestBody final TeamUserDto user
    ) {
        teamService.addUserToTeam(teamId, user);
        return ResponseEntity.ok(teamService.findAllTeamUsers(teamId));
    }

    @DeleteMapping(path = "/team/{teamId}/user/")
    public ResponseEntity<List<TeamUserDto>> deleteUserFromTeam(
            @PathVariable final String teamId,
            @RequestBody final TeamUserDto user
    ) {
        teamService.deleteUserFromTeam(teamId, user);
        return ResponseEntity.ok(teamService.findAllTeamUsers(teamId));
    }
    @GetMapping(path = "/team/{teamId}/user/")
    public ResponseEntity<List<TeamUserDto>> deleteUserFromTeam(
            @PathVariable final String teamId
    ) {
        return ResponseEntity.ok(teamService.findAllTeamUsers(teamId));
    }

}
