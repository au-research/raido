package au.org.raid.api.service;

import au.org.raid.api.dto.TeamUserDto;
import au.org.raid.api.factory.TeamFactory;
import au.org.raid.api.factory.TeamRecordFactory;
import au.org.raid.api.factory.TeamUserDtoFactory;
import au.org.raid.api.factory.record.TeamUserRecordFactory;
import au.org.raid.api.repository.TeamRepository;
import au.org.raid.api.repository.TeamUserRepository;
import au.org.raid.idl.raidv2.model.TeamCreateRequest;
import au.org.raid.idl.raidv2.model.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamRecordFactory teamRecordFactory;
    private final TeamFactory teamFactory;
    private final TeamUserRepository teamUserRepository;
    private final TeamUserRecordFactory teamUserRecordFactory;
    private final TeamUserDtoFactory teamUserDtoFactory;

    public Team create(final TeamCreateRequest createRequest, final Long servicePointId) {
        final var record = teamRepository.create(teamRecordFactory.create(createRequest, servicePointId));
        return teamFactory.create(record);
    }

    public List<Team> findAllByServicePointId(final Long servicePointId) {
        return teamRepository.findAllByServicePointId(servicePointId)
                .stream()
                .map(teamFactory::create)
                .toList();
    }

    public Optional<Team> findByIdAndServicePointId(final String id, final Long servicePointId) {
        return teamRepository.findByIdAndServicePointId(id, servicePointId)
                .map(teamFactory::create)
                .or(Optional::empty);
    }

    public Optional<Team> updateById(
            final String id,
            final Team teamDto) {

        if ( teamRepository.findById(id).isEmpty()) {
            return Optional.empty();
        }

        final var record = teamRepository.updateById(id, teamRecordFactory.create(teamDto));

        return Optional.of(teamFactory.create(record));
    }

    public void addUserToTeam(final String teamId, final TeamUserDto user) {
        //TODO: Validate user is member of same service point as team
        teamUserRepository.addUserToTeam(teamUserRecordFactory.create(user));
    }

    public List<TeamUserDto> findAllTeamUsers(final String teamId) {
        return teamUserRepository.findAllByTeamId(teamId).stream()
                .map(teamUserDtoFactory::create)
                .toList();
    }

    public void deleteUserFromTeam(final String teamId, final TeamUserDto user) {
        //TODO: Validate user is member of same service point as team
        teamUserRepository.deleteUserFromTeam(teamUserRecordFactory.create(user));

    }

    public Optional<Team> findById(final String id) {
        return teamRepository.findById(id)
                .map(teamFactory::create)
                .or(Optional::empty)
        ;
    }
}
