package au.org.raid.api.service;

import au.org.raid.api.dto.TeamDto;
import au.org.raid.api.dto.TeamUserDto;
import au.org.raid.api.exception.ValidationException;
import au.org.raid.api.factory.TeamDtoFactory;
import au.org.raid.api.factory.TeamRecordFactory;
import au.org.raid.api.factory.TeamUserDtoFactory;
import au.org.raid.api.factory.record.TeamUserRecordFactory;
import au.org.raid.api.repository.TeamRepository;
import au.org.raid.api.repository.TeamUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamRecordFactory teamRecordFactory;
    private final TeamDtoFactory teamDtoFactory;
    private final TeamUserRepository teamUserRepository;
    private final TeamUserRecordFactory teamUserRecordFactory;
    private final TeamUserDtoFactory teamUserDtoFactory;

    public TeamDto create(final TeamDto teamDto, final Long servicePointId) {
        teamDto.setId(UUID.randomUUID().toString());
        teamDto.setServicePointId(servicePointId);
        final var record = teamRepository.create(teamRecordFactory.create(teamDto));
        return teamDtoFactory.create(record);
    }

    public List<TeamDto> findAllByServicePointId(final Long servicePointId) {
        return teamRepository.findAllByServicePointId(servicePointId)
                .stream()
                .map(teamDtoFactory::create)
                .toList();
    }

    public Optional<TeamDto> findByIdAndServicePointId(final String id, final Long servicePointId) {
        return teamRepository.findByIdAndServicePointId(id, servicePointId)
                .map(teamDtoFactory::create)
                .or(Optional::empty);
    }

    public Optional<TeamDto> updateByIdAndServicePointId(
            final String id,
            final Long servicePointId,
            final TeamDto teamDto) {

        if ( teamRepository.findByIdAndServicePointId(id, servicePointId).isEmpty()) {
            return Optional.empty();
        }

        final var record = teamRepository.updateByIdAndServicePointId(id, servicePointId,
                teamRecordFactory.create(teamDto));

        return Optional.of(teamDtoFactory.create(record));
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

    public Optional<TeamDto> findById(final String id) {
        return teamRepository.findById(id)
                .map(teamDtoFactory::create)
                .or(Optional::empty)
        ;
    }
}
