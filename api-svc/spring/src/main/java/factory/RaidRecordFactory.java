package factory;

import org.springframework.stereotype.Component;
import raido.apisvc.service.apids.model.ApidsMintResponse;
import raido.db.jooq.api_svc.tables.records.RaidRecord;
import raido.db.jooq.api_svc.tables.records.ServicePointRecord;
import raido.idl.raidv2.model.CreateRaidV1Request;

@Component
public class RaidRecordFactory {
  public RaidRecord create(
    final CreateRaidV1Request raid,
    final ApidsMintResponse apidsMintResponse,
    final ServicePointRecord servicePointRecord) {

    /*
      handle,
      request.getMintRequest().getServicePointId(),
      raidUrl,
      apidsResponse.identifier.property.index,
      raidData.primaryTitle(),
      request.getMetadata(),
      mapApi2Db(request.getMetadata().getMetadataSchema()),
      raidData.startDate(),
      raidData.confidential
     */

    final RaidRecord raidRecord = new RaidRecord();
    raidRecord.setHandle(apidsMintResponse.identifier.handle);
    raidRecord.setServicePointId(servicePointRecord.getId());

    return null;

  }
}
