package raid.v2_api_migration

import raid.ddb_migration.JooqExec
import raido.idl.raidv2.model.AccessBlock
import raido.idl.raidv2.model.AccessType
import raido.idl.raidv2.model.DatesBlock
import raido.idl.raidv2.model.DescriptionBlock
import raido.idl.raidv2.model.DescriptionType
import raido.idl.raidv2.model.IdBlock
import raido.idl.raidv2.model.MetadataSchemaV1
import raido.idl.raidv2.model.Metaschema
import raido.idl.raidv2.model.MigrateLegacyRaidRequest
import raido.idl.raidv2.model.MigrateLegacyRaidRequestMintRequest
import raido.idl.raidv2.model.TitleBlock
import raido.idl.raidv2.model.TitleType

import static db.migration.jooq.tables.Raid.RAID
import static java.lang.Integer.parseInt

class Import1Raid {

  public static final String NOTRE_DAME = "University of Notre Dame Library"
  public static final String RDM = "RDM@UQ"

  static void main(String[] args) {
    importMostRecentRaid(NOTRE_DAME)
    importMostRecentRaid(RDM)
  }
  
  static void importMostRecentRaid(String svcPointName) {
    def exec = new JooqExec()
    var raid = null
    exec.withDb(db -> {
      raid = db.select().from(RAID).
        where(RAID.OWNER.eq(svcPointName)).
        orderBy(RAID.CREATION_DATE.desc()).
        limit(1).fetchOne()
      println "raid to import: $raid"
    })
    var startDate = raid.get(RAID.START_DATE)

    def raido = new RaidoApi()
    def servicePoint = raido.findServicePoint(svcPointName)
    println "service point to to import against" + servicePoint
    var migrateResult = raido.adminApi.migrateLegacyRaid( new MigrateLegacyRaidRequest().
      mintRequest(new MigrateLegacyRaidRequestMintRequest().
        servicePointId(servicePoint.id).
        contentIndex(parseInt(raid.getValue(RAID.CONTENT_INDEX))) ).
      metadata( new MetadataSchemaV1().
        metadataSchema(Metaschema.RAIDO_METADATA_SCHEMA_V1).
        id(new IdBlock().
          identifier(raid.get(RAID.HANDLE)).
          identifierTypeUri("https://raid.org").
          globalUrl(raid.getValue(RAID.CONTENT_PATH)) ).
        access(new AccessBlock().
          type(AccessType.CLOSED).
          accessStatement("closed by data migration process") ).
        dates( new DatesBlock().startDate(startDate.toLocalDate()) ).
        titles([new TitleBlock().
          type(TitleType.PRIMARY_TITLE).
          title(raid.getValue(RAID.NAME)).
          startDate(startDate.toLocalDate()) ]).
        descriptions([new DescriptionBlock().
          type(DescriptionType.PRIMARY_DESCRIPTION).
          description(raid.get(RAID.DESCRIPTION)) ])
      )
    )
    assert migrateResult.success == true : migrateResult.getFailures()
    println migrateResult
    
  }
}
