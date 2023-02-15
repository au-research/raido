package raido.apisvc.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;
import raido.apisvc.repository.dto.Raid;
import raido.apisvc.service.raid.MetadataService;
import raido.db.jooq.api_svc.tables.records.RaidRecord;
import raido.db.jooq.api_svc.tables.records.ServicePointRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static raido.apisvc.endpoint.Constant.MAX_EXPERIMENTAL_RECORDS;
import static raido.db.jooq.api_svc.tables.Raid.RAID;
import static raido.db.jooq.api_svc.tables.ServicePoint.SERVICE_POINT;

@Repository
public class RaidRepository {
  private final DSLContext dslContext;
  private final TransactionTemplate transactionTemplate;

  private final MetadataService metadataService;

  public RaidRepository(final DSLContext dslContext, final TransactionTemplate transactionTemplate, final MetadataService metadataService) {
    this.dslContext = dslContext;
    this.transactionTemplate = transactionTemplate;
    this.metadataService = metadataService;
  }

//  public void save(final String handle, final Long servicePointId, final String raidUrl, final Integer urlIndex,
//                   final String primaryTitle, final MetadataSchemaV1 metadata, final Metaschema metaschema,
//                   final LocalDate startDate, final boolean isConfidential) {
//
//    final String metadataJson;
//    try {
//      metadataJson = metadataService.mapToJson(metadata);
//    } catch (ValidationFailureException e) {
//      throw new ValidationException(e.getFailures());
//    }
//
//    transactionTemplate.executeWithoutResult((status) -> dslContext.insertInto(RAID).
//      set(RAID.HANDLE, handle).
//      set(RAID.SERVICE_POINT_ID, servicePointId).
//      set(RAID.URL, raidUrl).
//      set(RAID.URL_INDEX, urlIndex).
//      set(RAID.PRIMARY_TITLE, primaryTitle).
//      set(RAID.METADATA, JSONB.valueOf(metadataJson)).
//      set(RAID.METADATA_SCHEMA, metaschema).
//      set(RAID.START_DATE, startDate).
//      set(RAID.DATE_CREATED, LocalDateTime.now()).
//      set(RAID.CONFIDENTIAL, isConfidential).
//      execute());
//  }

  public void save(final RaidRecord raid) {
    transactionTemplate.executeWithoutResult((status) -> dslContext.insertInto(RAID).
      set(RAID.HANDLE, raid.getHandle()).
      set(RAID.SERVICE_POINT_ID, raid.getServicePointId()).
      set(RAID.URL, raid.getUrl()).
      set(RAID.URL_INDEX, raid.getUrlIndex()).
      set(RAID.PRIMARY_TITLE, raid.getPrimaryTitle()).
      set(RAID.METADATA, raid.getMetadata()).
      set(RAID.METADATA_SCHEMA, raid.getMetadataSchema()).
      set(RAID.START_DATE, raid.getStartDate()).
      set(RAID.DATE_CREATED, LocalDateTime.now()).
      set(RAID.CONFIDENTIAL, raid.getConfidential()).
      execute());
  }

//  public void update(final String handle,
//                   final String primaryTitle, final MetadataSchemaV1 metadata, final Metaschema metaschema,
//                   final LocalDate startDate, final boolean isConfidential) {
//
//    final String metadataJson;
//    try {
//      metadataJson = metadataService.mapToJson(metadata);
//    } catch (ValidationFailureException e) {
//      throw new ValidationException(e.getFailures());
//    }
//
//    transactionTemplate.executeWithoutResult((status) -> dslContext.update(RAID).
//      set(RAID.PRIMARY_TITLE, primaryTitle).
//      set(RAID.METADATA, JSONB.valueOf(metadataJson)).
//      set(RAID.METADATA_SCHEMA, metaschema).
//      set(RAID.START_DATE, startDate).
//      set(RAID.DATE_CREATED, LocalDateTime.now()).
//      set(RAID.CONFIDENTIAL, isConfidential).
//      where(RAID.HANDLE.eq(handle)).
//      execute());
//  }

  public Optional<Raid> findByHandle(final String handle) {
    return dslContext.select(RAID.fields()).
      select(SERVICE_POINT.fields()).
      from(RAID).join(SERVICE_POINT).onKey().
      where(RAID.HANDLE.eq(handle)).
      fetchOptional(record -> new Raid(
        record.into(RaidRecord.class), record.into(ServicePointRecord.class)
      ));
  }

  public List<Raid> findAllByServicePointId(final Long servicePointId) {
    return dslContext.select(RAID.fields()).
      select(SERVICE_POINT.fields()).
      from(RAID).join(SERVICE_POINT).onKey().
      where(
        RAID.SERVICE_POINT_ID.eq(servicePointId)
      ).
      orderBy(RAID.DATE_CREATED.desc()).
      limit(MAX_EXPERIMENTAL_RECORDS).
      fetch(record -> new Raid(
        record.into(RaidRecord.class), record.into(ServicePointRecord.class)
      ));
  }
}
