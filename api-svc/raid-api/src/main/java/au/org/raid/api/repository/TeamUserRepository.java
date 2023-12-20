package au.org.raid.api.repository;

import au.org.raid.api.dto.TeamUserDto;
import au.org.raid.db.jooq.tables.records.TeamUserRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static au.org.raid.db.jooq.tables.TeamUser.TEAM_USER;

@Repository
@RequiredArgsConstructor
public class TeamUserRepository {
    private final DSLContext dslContext;

    public TeamUserRecord addUserToTeam(final TeamUserRecord record) {
        return dslContext.insertInto(TEAM_USER)
                .set(TEAM_USER.TEAM_ID, record.getTeamId())
                .set(TEAM_USER.APP_USER_ID, record.getAppUserId())
                .returning()
                .fetchOne();
    }

    public List<TeamUserRecord> findAllByTeamId(final String teamId) {
        return dslContext.selectFrom(TEAM_USER)
                .where(TEAM_USER.TEAM_ID.eq(teamId))
                .fetch();
    }

    public void deleteUserFromTeam(final TeamUserRecord record) {
        dslContext.deleteFrom(TEAM_USER)
                .where(TEAM_USER.APP_USER_ID.eq(record.getAppUserId()))
                .execute();
    }
}
