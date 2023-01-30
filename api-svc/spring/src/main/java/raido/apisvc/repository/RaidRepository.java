package raido.apisvc.repository;

import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;
import raido.apisvc.service.raid.MetadataService;
import raido.apisvc.service.raid.ValidationFailureException;
import raido.db.jooq.api_svc.enums.Metaschema;
import raido.idl.raidv2.model.CreateMetadataSchemaV1;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static raido.db.jooq.api_svc.tables.Raid.RAID;

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

  public void save(final String handle, final Long servicePointId, final String raidUrl, final Integer urlIndex,
                   final String primaryTitle, final CreateMetadataSchemaV1 metadata, final Metaschema metaschema,
                   final LocalDate startDate, final boolean isConfidential) throws ValidationFailureException {

    final var metadataJson = metadataService.mapToJson(metadata);

    transactionTemplate.executeWithoutResult((status) -> dslContext.insertInto(RAID).
      set(RAID.HANDLE, handle).
      set(RAID.SERVICE_POINT_ID, servicePointId).
      set(RAID.URL, raidUrl).
      set(RAID.URL_INDEX, urlIndex).
      set(RAID.PRIMARY_TITLE, primaryTitle).
      set(RAID.METADATA, JSONB.valueOf(metadataJson)).
      set(RAID.METADATA_SCHEMA, metaschema).
      set(RAID.START_DATE, startDate).
      set(RAID.DATE_CREATED, LocalDateTime.now()).
      set(RAID.CONFIDENTIAL, isConfidential).
      execute());
  }
}
