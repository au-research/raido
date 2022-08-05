package raid.ddb_migration

import db.migration.jooq.tables.FlywaySchemaHistory
import db.migration.jooq.tables.records.AssociationIndexRecord
import db.migration.jooq.tables.records.MetadataRecord
import db.migration.jooq.tables.records.RaidRecord
import db.migration.jooq.tables.records.TokenRecord
import org.jooq.DSLContext
import org.jooq.JSONB
import org.jooq.impl.DSL

import java.time.LocalDateTime
import java.util.function.Consumer

import static db.migration.jooq.tables.AssociationIndex.ASSOCIATION_INDEX
import static db.migration.jooq.tables.FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY
import static db.migration.jooq.tables.ImportHistory.IMPORT_HISTORY
import static db.migration.jooq.tables.Metadata.METADATA
import static db.migration.jooq.tables.Raid.RAID
import static db.migration.jooq.tables.Token.TOKEN
import static raid.ddb_migration.Util.*

class MigrateRaidTableItems {
  public static final String ddbS3ExportTimestampFormat = "yyyy-MM-dd HH:mm:ss"
  /* 2020-01-20T06:17:28.453321
  ignore the microseconds, we don't care */
  public static final String ddbS3ExportIsoTimestampFormat =
    "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"

  static RaidRecord mapRaidTableRecord(
    DSLContext db, DdbS3ExportLine iItem
  ) {
    def record = db.newRecord(RAID).
      setHandle(iItem.json.Item.handle as String).
      setOwner(iItem.json.Item.owner as String).
      setContentPath(iItem.json.Item.contentPath as String).
      setContentIndex(iItem.json.Item.contentIndex as String).
      setStartDate(parseDdbS3Timestamp(iItem.json.Item.startDate)).
      setCreationDate(parseDdbS3Timestamp(iItem.json.Item.creationDate)).
      setS3Export(JSONB.jsonb(formalJson(iItem.json)))

    if( !iItem.json.Item.meta ){
      record.setName("").setDescription("")
    }
    else if( !iItem.json.Item.meta.description ){
      record.setName(iItem.json.Item.meta.name as String).
        setDescription("")
    }
    else{
      record.setName(iItem.json.Item.meta.name as String).
        setDescription(iItem.json.Item.meta.description as String)
    }

    return record
  }

  static AssociationIndexRecord mapAssocIndexTableRecord(
    DSLContext db, DdbS3ExportLine iItem
  ) {
    def record = db.newRecord(ASSOCIATION_INDEX).
      setHandle(iItem.json.Item.handle as String).
      setOwnerName(iItem.json.Item.name as String).
      setRaidName(iItem.json.Item.raidName as String).
      setType(iItem.json.Item.type as String).
      setStartDate(parseDdbS3Timestamp(iItem.json.Item.startDate as String)).
      setS3Export(JSONB.jsonb(formalJson(iItem.json)))

    if( iItem.json.Item.role ){
      record.setRole(iItem.json.Item.role as String)
    }
    else{
      record.setRole("")
    }

    return record
  }

  static MetadataRecord mapMetadataTableRecord(
    DSLContext db, DdbS3ExportLine iItem
  ) {
    def record = db.newRecord(METADATA).
      setName(iItem.json.Item.name as String).
      setType(iItem.json.Item.type as String).
      setGrid(iItem.json.Item.grid as String ?: "").
      setIsni(iItem.json.Item.isni as String ?: "").
      setAdminEmail(iItem.json.Item.adminContactEmailAddress as String ?: "").
      setTechEmail(iItem.json.Item.technicalContactEmailAddress as String ?: "").
      setS3Export(JSONB.jsonb(formalJson(iItem.json)))

    return record
  }

  static TokenRecord mapTokenTableRecord(
    DSLContext db, DdbS3ExportLine iItem
  ) {
    def record = db.newRecord(TOKEN).
      setName(iItem.json.Item.name as String).
      setEnvironment(iItem.json.Item.environment as String).
      setDateCreated(parseDdbS3IsoTimestamp(
        iItem.json.Item.dateCreated as String)).
      setToken(iItem.json.Item.token as String).
      setS3Export(JSONB.jsonb(formalJson(iItem.json)))

    return record
  }

  static void recordMigration(Map runDetails, Consumer<JooqExec> fn) {
    LocalDateTime runStamp = LocalDateTime.now()

    def exec = new JooqExec()

    def flywayVersion = exec.withDb(db -> {
      return db.select().
        from(FLYWAY_SCHEMA_HISTORY).
        orderBy(FLYWAY_SCHEMA_HISTORY.INSTALLED_ON.desc()).
        limit(1).fetchSingle().intoMap()
    })
    
    runDetails.flywayVersion = [
      version: flywayVersion.version,
      description: flywayVersion.description,
      script: flywayVersion.script,
      installedOn: flywayVersion.installedOn,
    ] 
    
    exec.withDb(db -> {
      db.insertInto(IMPORT_HISTORY).
        columns(
          IMPORT_HISTORY.ACTION_DATE, 
          IMPORT_HISTORY.STATUS,
          IMPORT_HISTORY.DETAILS
        ).values(
          runStamp,
          'running',
          JSONB.jsonb(prettyJson(runDetails))
        ).execute()
    })

    try {
      fn.accept(exec)

      exec.withDb(db -> {
        db.update(IMPORT_HISTORY).
          set(IMPORT_HISTORY.STATUS, "finished").
          where(IMPORT_HISTORY.ACTION_DATE.eq(runStamp)).
          execute()
      })

    }
    catch( e ){
      exec.withDb(db -> {
        db.update(IMPORT_HISTORY).
          set(IMPORT_HISTORY.STATUS, "failed").
          set(IMPORT_HISTORY.ERROR, e.message).
          where(IMPORT_HISTORY.ACTION_DATE.eq(runStamp)).
          execute()
      })
    }

  }

  private static LocalDateTime parseDdbS3Timestamp(String value) {
    // known via observation
    return parseDate(ddbS3ExportTimestampFormat, Util.sydneyZone, value)
  }

  private static LocalDateTime parseDdbS3IsoTimestamp(String value) {
    // timezone is a guess, not sure - doesn't matter anyway
    return parseDate(ddbS3ExportIsoTimestampFormat, Util.utcZone, value)
  }

}