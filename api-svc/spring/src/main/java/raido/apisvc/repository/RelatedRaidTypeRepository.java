package raido.apisvc.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import raido.db.jooq.api_svc.tables.records.RelatedRaidTypeRecord;

import java.util.Optional;

import static raido.db.jooq.api_svc.tables.RelatedRaidType.RELATED_RAID_TYPE;
@Repository
public class RelatedRaidTypeRepository {
  private final DSLContext dslContext;

  public RelatedRaidTypeRepository(final DSLContext dslContext) {
    this.dslContext = dslContext;
  }

  public Optional<RelatedRaidTypeRecord> findByUrl(final String url) {
    return dslContext.select(RELATED_RAID_TYPE.fields())
      .from(RELATED_RAID_TYPE)
      .where(RELATED_RAID_TYPE.URL.eq(url)).
      fetchOptional(record -> new RelatedRaidTypeRecord()
        .setName(RELATED_RAID_TYPE.NAME.getValue(record))
        .setDescription(RELATED_RAID_TYPE.DESCRIPTION.getValue(record))
        .setUrl(RELATED_RAID_TYPE.URL.getValue(record))
      );
  }
}
