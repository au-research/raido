package scratch

import org.junit.jupiter.api.Test

import java.time.LocalDateTime

import static db.migration.jooq.tables.Raid.RAID
import static raid.ddb_migration.JooqExec.WithDb
import static raid.ddb_migration.Util.inSydneyZone

class StoRaidTableMigration {

  // expects data to have been imported
  @Test 
  void query(){
    def result = WithDb(db->{
      return db.select(RAID.NAME, RAID.CREATION_DATE).
        from(RAID).
        where(RAID.NAME.likeIgnoreCase("sto%")).
        fetch()
    })
    
    result.each{ it->{
      LocalDateTime iCreateDate = it.get(RAID.CREATION_DATE)
      println it.get(RAID.NAME) +   
        "\n  database time: " + iCreateDate +
        "\n  sydney time: " + inSydneyZone(iCreateDate)  
    }}
  }
}
