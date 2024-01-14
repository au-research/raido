package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.ServicePointRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static au.org.raid.db.jooq.tables.ServicePoint.SERVICE_POINT;

@Repository
@RequiredArgsConstructor
public class ServicePointRepository {
    private final DSLContext dslContext;

    public Optional<ServicePointRecord> findById(final long servicePointId) {
        return dslContext.selectFrom(SERVICE_POINT)
                .where(SERVICE_POINT.ID.eq(servicePointId))
                .fetchOptional();
    }

    public ServicePointRecord create(final ServicePointRecord record) {
        return dslContext.insertInto(SERVICE_POINT)
                .set(SERVICE_POINT.NAME, record.getName())
                .set(SERVICE_POINT.ADMIN_EMAIL, record.getAdminEmail())
                .set(SERVICE_POINT.ENABLED, record.getEnabled())
                .set(SERVICE_POINT.APP_WRITES_ENABLED, record.getAppWritesEnabled())
                .set(SERVICE_POINT.TECH_EMAIL, record.getTechEmail())
                .set(SERVICE_POINT.IDENTIFIER_OWNER, record.getIdentifierOwner())
                .set(SERVICE_POINT.SEARCH_CONTENT, record.getSearchContent())
                .returning()
                .fetchOne();
    }

    public ServicePointRecord update(final ServicePointRecord record) {
        return dslContext.update(SERVICE_POINT)
                .set(SERVICE_POINT.NAME, record.getName())
                .set(SERVICE_POINT.ADMIN_EMAIL, record.getAdminEmail())
                .set(SERVICE_POINT.ENABLED, record.getEnabled())
                .set(SERVICE_POINT.APP_WRITES_ENABLED, record.getAppWritesEnabled())
                .set(SERVICE_POINT.TECH_EMAIL, record.getTechEmail())
                .set(SERVICE_POINT.IDENTIFIER_OWNER, record.getIdentifierOwner())
                .set(SERVICE_POINT.SEARCH_CONTENT, record.getSearchContent())
                .where(SERVICE_POINT.ID.eq(record.getId()))
                .returning()
                .fetchOne();
    }

    public List<ServicePointRecord> findAll() {
        return dslContext.selectFrom(SERVICE_POINT)
                .fetch();
    }
}