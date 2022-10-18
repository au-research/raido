package raido.apisvc.service;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;
import raido.db.jooq.api_svc.tables.records.RaidRecord;
import raido.db.jooq.api_svc.tables.records.ServicePointRecord;

import static raido.db.jooq.api_svc.tables.Raid.RAID;
import static raido.db.jooq.api_svc.tables.ServicePoint.SERVICE_POINT;

@Component
public class RaidService {
  
  private DSLContext db;

  public RaidService(DSLContext db) {
    this.db = db;
  }

  public record ReadRaidData(
    RaidRecord raid, 
    ServicePointRecord servicePoint
  ){}

  public ReadRaidData readRaidData(String handle){
    return db.select(RAID.fields()).
      select(SERVICE_POINT.fields()).
      from(RAID).join(SERVICE_POINT).onKey().
      where(RAID.HANDLE.eq(handle)).
      fetchSingle(r -> new ReadRaidData(
        r.into(RaidRecord.class), 
        r.into(ServicePointRecord.class)) );
  }
}
