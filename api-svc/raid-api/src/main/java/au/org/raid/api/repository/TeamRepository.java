package au.org.raid.api.repository;

import au.org.raid.api.dto.TeamUserDto;
import au.org.raid.db.jooq.tables.records.TeamRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static au.org.raid.db.jooq.tables.Team.TEAM;

@Repository
@RequiredArgsConstructor
public class TeamRepository {
    private final DSLContext dslContext;
    public TeamRecord create(final TeamRecord record) {
        return dslContext.insertInto(TEAM)
                .set(TEAM.ID, record.getId())
                .set(TEAM.NAME, record.getName())
                .set(TEAM.PREFIX, record.getPrefix())
                .set(TEAM.SERVICE_POINT_ID, record.getServicePointId())
                .returning()
                .fetchOne();
    }

    public List<TeamRecord> findAllByServicePointId(final Long servicePointId) {
        return dslContext.selectFrom(TEAM)
                .where(TEAM.SERVICE_POINT_ID.eq(servicePointId))
                .fetch();
    }

    public Optional<TeamRecord> findByIdAndServicePointId(final String id, final Long servicePointId) {
        return dslContext.selectFrom(TEAM)
                .where(TEAM.ID.eq(id))
                .and(TEAM.SERVICE_POINT_ID.eq(servicePointId))
                .fetchOptional();
    }

    public TeamRecord updateByIdAndServicePointId(final String id, final Long servicePointId, final TeamRecord record) {
        return dslContext.update(TEAM)
                .set(TEAM.NAME, record.getName())
                .set(TEAM.PREFIX, record.getPrefix())
                .where(TEAM.ID.eq(id))
                .and(TEAM.SERVICE_POINT_ID.eq(servicePointId))
                .returning()
                .fetchOne();
    }

    public Optional<TeamRecord> findById(final String id) {
        return dslContext.selectFrom(TEAM)
                .where(TEAM.ID.eq(id))
                .fetchOptional();
    }
}
