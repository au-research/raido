package raido.apisvc.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;
import raido.apisvc.repository.dto.Raid;
import raido.db.jooq.api_svc.tables.records.RaidRecord;
import raido.db.jooq.api_svc.tables.records.ServicePointRecord;

import java.util.Optional;

import static raido.db.jooq.api_svc.tables.Raid.RAID;
import static raido.db.jooq.api_svc.tables.ServicePoint.SERVICE_POINT;

@Repository
public class ServicePointRepository {
  private final DSLContext dslContext;

  public ServicePointRepository(final DSLContext dslContext, final TransactionTemplate transactionTemplate) {
    this.dslContext = dslContext;
  }

  public Optional<ServicePointRecord> findById(final long servicePointId) {
    return dslContext.select(SERVICE_POINT.fields()).
      from(SERVICE_POINT).
      where(SERVICE_POINT.ID.eq(servicePointId)).
      fetchOptional(record -> new ServicePointRecord()
        .setId(SERVICE_POINT.ID.getValue(record))
        .setIdentifierOwner(SERVICE_POINT.IDENTIFIER_OWNER.getValue(record))
        .setName(SERVICE_POINT.NAME.getValue(record))
        .setEnabled(SERVICE_POINT.ENABLED.getValue(record))
        .setAdminEmail(SERVICE_POINT.ADMIN_EMAIL.getValue(record))
        .setTechEmail(SERVICE_POINT.TECH_EMAIL.getValue(record))
        .setLowerName(SERVICE_POINT.LOWER_NAME.getValue(record))
        .setSearchContent(SERVICE_POINT.SEARCH_CONTENT.getValue(record))
      );
  }
}
