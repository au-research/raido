package raido.apisvc.service.auth.admin;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;
import raido.apisvc.util.Log;
import raido.idl.raidv2.model.ServicePoint;

import static raido.apisvc.util.Log.to;
import static raido.db.jooq.api_svc.tables.ServicePoint.SERVICE_POINT;

@Component
public class ServicePointService {
  private static final Log log = to(ServicePointService.class);

  private DSLContext db;

  public ServicePointService(DSLContext db) {
    this.db = db;
  }

  public ServicePoint updateServicePoint(
    ServicePoint req
  ) {
    var record = req.getId() == null ?
      db.newRecord(SERVICE_POINT) :
      db.fetchSingle(SERVICE_POINT, SERVICE_POINT.ID.eq(req.getId()));

    record.setName(req.getName());
    record.setIdentifierOwner(req.getIdentifierOwner());
    record.setSearchContent(req.getSearchContent());
    record.setAdminEmail(req.getAdminEmail());
    record.setTechEmail(req.getTechEmail());
    record.setEnabled(req.getEnabled());
    record.setAppWritesEnabled(req.getAppWritesEnabled());

    if( req.getId() == null ){
      record.insert();
    }
    else {
      record.setId(req.getId());
      record.update();
    }

    return new ServicePoint().
      id(record.getId()).
      name((record.getName())).
      identifierOwner((record.getIdentifierOwner())).
      searchContent(record.getSearchContent()).
      adminEmail(record.getAdminEmail()).
      techEmail(record.getTechEmail()).
      enabled(record.getEnabled());
  }
}
