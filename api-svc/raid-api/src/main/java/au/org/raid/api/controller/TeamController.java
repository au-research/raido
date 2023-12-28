package au.org.raid.api.controller;

import au.org.raid.api.dto.TeamUserDto;
import au.org.raid.api.service.TeamService;
import au.org.raid.idl.raidv2.api.TeamApi;
import au.org.raid.idl.raidv2.model.Team;
import au.org.raid.idl.raidv2.model.TeamCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TeamController implements TeamApi {
    private final TeamService teamService;

    public ResponseEntity<Team> createTeam(
            @PathVariable final Long servicePointId,
            @RequestBody final TeamCreateRequest createRequest) {
        final var team = teamService.create(createRequest, servicePointId);

        final var uri = "/service-point/%d/team/%s".formatted(servicePointId, team.getId());

        return ResponseEntity.created(URI.create(uri)).body(team);
    }

    public ResponseEntity<List<Team>> findAllTeamsByServicePointId(
            @PathVariable final Long servicePointId) {
        final var teams = teamService.findAllByServicePointId(servicePointId);

        return ResponseEntity.ok(teams);
    }

    public ResponseEntity<Team> findTeamById(
            @PathVariable final String id
    ) {
        return ResponseEntity.of(teamService.findById(id));
    }

    public ResponseEntity<Team> updateTeam(
            @PathVariable final String id,
            @RequestBody final Team team
    ) {
        final var saved = teamService.updateById(id, team);

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
