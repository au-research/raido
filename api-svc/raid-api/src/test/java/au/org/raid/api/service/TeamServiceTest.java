package au.org.raid.api.service;

import au.org.raid.api.dto.TeamUserDto;
import au.org.raid.api.factory.TeamFactory;
import au.org.raid.api.factory.TeamRecordFactory;
import au.org.raid.api.factory.TeamUserDtoFactory;
import au.org.raid.api.factory.record.TeamUserRecordFactory;
import au.org.raid.api.repository.TeamRepository;
import au.org.raid.api.repository.TeamUserRepository;
import au.org.raid.db.jooq.tables.records.TeamRecord;
import au.org.raid.db.jooq.tables.records.TeamUserRecord;
import au.org.raid.idl.raidv2.model.Team;
import au.org.raid.idl.raidv2.model.TeamCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TeamRecordFactory teamRecordFactory;
    @Mock
    private TeamFactory teamFactory;
    @Mock
    private TeamUserRepository teamUserRepository;
    @Mock
    private TeamUserRecordFactory teamUserRecordFactory;
    @Mock
    private TeamUserDtoFactory teamUserDtoFactory;
    @InjectMocks
    private TeamService teamService;

    @Test
    @DisplayName("create() saves new team")
    void create() {
        final var servicePointId = 123L;
        final var request = new TeamCreateRequest();
        final var teamRecord = new TeamRecord();
        final var saved = new TeamRecord();
        final var team = new Team();

        when(teamRecordFactory.create(request, servicePointId)).thenReturn(teamRecord);
        when(teamRepository.create(teamRecord)).thenReturn(saved);
        when(teamFactory.create(saved)).thenReturn(team);

        assertThat(teamService.create(request, servicePointId), is(team));
    }

    @Test
    @DisplayName("findAllByServicePointId() returns all teams for a service point")
    void findAllByServicePointId() {
        final var servicePointId = 123L;
        final var teamRecord = new TeamRecord();
        final var team = new Team();

        when(teamRepository.findAllByServicePointId(servicePointId)).thenReturn(List.of(teamRecord));
        when(teamFactory.create(teamRecord)).thenReturn(team);

        assertThat(teamService.findAllByServicePointId(servicePointId), is(List.of(team)));
    }

    @Test
    @DisplayName("findAllByServicePointId() returns empty list if none found")
    void findAllByServicePointIdReturnsEmptyList() {
        final var servicePointId = 123L;

        when(teamRepository.findAllByServicePointId(servicePointId)).thenReturn(Collections.emptyList());

        assertThat(teamService.findAllByServicePointId(servicePointId), is(Collections.emptyList()));
        verifyNoInteractions(teamFactory);
    }

    @Test
    @DisplayName("updateById() saves and returns team")
    void updateById() {
        final var id = "_id";
        final var team = new Team();
        final var updatedTeam = new Team();
        final var found = new TeamRecord();
        final var record = new TeamRecord();
        final var updated = new TeamRecord();

        when(teamRepository.findById(id)).thenReturn(Optional.of(found));
        when(teamRecordFactory.create(team)).thenReturn(record);
        when(teamRepository.updateById(id, record)).thenReturn(updated);
        when(teamFactory.create(updated)).thenReturn(updatedTeam);

        assertThat(teamService.updateById(id, team), is(Optional.of(updatedTeam)));
    }

    @Test
    @DisplayName("updateById() returns empty optional if team not found")
    void updateByIdReturnsEmptyOptional() {
        final var id = "_id";
        final var team = new Team();

        when(teamRepository.findById(id)).thenReturn(Optional.empty());

        assertThat(teamService.updateById(id, team), is(Optional.empty()));

        verifyNoInteractions(teamRecordFactory);
        verifyNoMoreInteractions(teamRepository);
        verifyNoInteractions(teamFactory);
    }

    @Test
    @DisplayName("Add a user to a team")
    void addUserToTeam() {
        final var teamId = "team-id";
        final var user = new TeamUserDto();
        final var record = new TeamUserRecord();

        when(teamUserRecordFactory.create(user)).thenReturn(record);

        teamService.addUserToTeam(teamId, user);

        verify(teamUserRepository).addUserToTeam(record);
    }

    @Test
    @DisplayName("Returns a list of all team users")
    void findAllTeamUsers() {
        final var teamId = "team-id";
        final var record = new TeamUserRecord();
        final var user = new TeamUserDto();

        when(teamUserRepository.findAllByTeamId(teamId)).thenReturn(List.of(record));
        when(teamUserDtoFactory.create(record)).thenReturn(user);

        assertThat(teamService.findAllTeamUsers(teamId), is(List.of(user)));
    }

    @Test
    @DisplayName("Deletes user from a team")
    void deleteUserFromTeam() {
        final var teamId = "team-id";
        final var user = new TeamUserDto();
        final var record = new TeamUserRecord();

        when(teamUserRecordFactory.create(user)).thenReturn(record);

        teamService.deleteUserFromTeam(teamId, user);

        verify(teamUserRepository).deleteUserFromTeam(record);
    }

    @Test
    @DisplayName("Returns optional team")
    void findById() {
        final var id = "_id";
        final var record = new TeamRecord();
        final var team = new Team();

        when(teamRepository.findById(id)).thenReturn(Optional.of(record));
        when(teamFactory.create(record)).thenReturn(team);

        assertThat(teamService.findById(id), is(Optional.of(team)));
    }

    @Test
    @DisplayName("Returns empty optional if no team found")
    void findByIdReturnsEmptyOptional() {
        final var id = "_id";

        when(teamRepository.findById(id)).thenReturn(Optional.empty());
        assertThat(teamService.findById(id), is(Optional.empty()));
        verifyNoInteractions(teamFactory);
    }
}