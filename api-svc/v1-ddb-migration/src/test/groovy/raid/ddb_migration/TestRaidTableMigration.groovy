package raid.ddb_migration

import org.junit.jupiter.api.Test

import java.time.ZoneId

import static raid.ddb_migration.MigrateRaidTableItems.ddbS3ExportTimestampFormat
import static raid.ddb_migration.Util.parseDate
import static raid.ddb_migration.Util.sydneyZone

class TestRaidTableMigration {
  
  @Test
  void parseFile(){
    new DdbS3ExportFile().parseFile(
      "RaidTableExample.json", 
      this.class.getResource('RaidTableExample.json') )
    println "finished"
  }
  
  @Test 
  void parseTimestamp(){
    println "TZ: " + ZoneId.systemDefault().toString()
    def ldt = parseDate(
      ddbS3ExportTimestampFormat, sydneyZone, "2021-11-29 17:01:07")
    println "ldt: " + ldt
    println "ldt.d: " + ldt.toDate()
    assert ldt.toString() == "2021-11-29T06:01:07"
  }
  
}
