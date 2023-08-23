package au.org.raid.api.service.export;

import au.org.raid.api.service.raid.MetadataService;
import au.org.raid.api.service.raid.ValidationFailureException;
import au.org.raid.api.spring.config.environment.AgencyPublicDataExportProps;
import au.org.raid.api.util.Log;
import au.org.raid.api.util.Nullable;
import au.org.raid.db.jooq.api_svc.tables.records.RaidRecord;
import au.org.raid.idl.raidv2.model.PublicReadRaidMetadataResponseV1;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Select;
import org.jooq.SelectLimitStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import static au.org.raid.api.util.ExceptionUtil.wrapException;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.db.jooq.api_svc.tables.Raid.RAID;

/**
 Writes output file as "NDJSON": http://ndjson.org/
 */
@Component
public class AgencyPublicDataExportService {
  private static final Log log = to(AgencyPublicDataExportService.class);

  private DSLContext db;
  private AgencyPublicDataExportProps props;
  private MetadataService metaSvc;
  
  public AgencyPublicDataExportService(
    DSLContext db, 
    AgencyPublicDataExportProps props,
    MetadataService metaSvc
  ) {
    this.db = db;
    this.props = props;
    this.metaSvc = metaSvc;
  }

  /* fetchSize() is supposed to be in a TX, plus it makes the export consistent.
  But it means we'll be keeping a TX open for however long the export takes,
  it's not a problem that we hold a DB connection (because this runs outside
  the api-svc, from codebuild or wherever).
  But holding a tx open will mean that postgres vacuum won't be able to clean
  up any raid tuples while the export is in progress.
  When this becomes an issue, probably need to start breaking the export up
  into date-range based batches anyway.
  */
  @Transactional
  /**
   IMPROVE: rather than passing in before/after and other conditions, might
   makes sense to factor out a Condition param, callers can then setup 
   whatever conditions they want and this method can just focus on iteration
   and writing the data.
   
   @param after may be null, in which case no restriction
   @param before may be null, in which case no restriction
   @param maxRows may be null, in which case all rows are exported
   */
  public void exportData(
    BufferedWriter writer,
    @Nullable LocalDateTime after,
    @Nullable LocalDateTime before,
    @Nullable Integer maxRows
  ) {
    Condition afterCondition = after == null ? DSL.noCondition() :
      DSL.condition(RAID.DATE_CREATED.gt(after));

    Condition beforeCondition = before == null ? DSL.noCondition() :
      DSL.condition(RAID.DATE_CREATED.lt(before));

    // optional limit workaround: https://stackoverflow.com/a/66442511/924597
    Select<RaidRecord> fetch;
    SelectLimitStep<RaidRecord> limit;

    fetch = limit = db.selectFrom(RAID).
      where(beforeCondition.and(afterCondition)).
      orderBy(RAID.DATE_CREATED.desc());

    if( maxRows != null ){
      fetch = limit.limit(maxRows);
    }

    var export = new RaidExportWatcher<RaidRecord>(iRecord->{
      var publicData = metaSvc.mapPublicReadMetadataResponse(iRecord);
      writeRecord(writer, publicData);
    });
    
    export.startProgressLogging(props.logPeriodSeconds);
    try {
      fetch.fetchSize(props.fetchSize).fetch().forEach(export::watch);
    }
    finally {
      export.stopProgressLogging();
    }

    export.logSummary();
  }

  private void writeRecord(
    BufferedWriter writer,
    PublicReadRaidMetadataResponseV1 publicData
  ) {
    try {
      writer.write(metaSvc.mapToJson(publicData));
      writer.newLine();
    }
    catch( IOException | ValidationFailureException ex ){
      throw wrapException(ex, "while writing record");
    }
  }

}
