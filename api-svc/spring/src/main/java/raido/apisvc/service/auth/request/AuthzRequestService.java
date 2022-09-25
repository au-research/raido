package raido.apisvc.service.auth.request;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Component;
import raido.apisvc.endpoint.Constant;
import raido.apisvc.util.Log;
import raido.db.jooq.api_svc.enums.AuthRequestStatus;
import raido.idl.raidv2.model.AuthzRequest;
import raido.idl.raidv2.model.AuthzRequestStatus;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static org.jooq.impl.DSL.asterisk;
import static raido.apisvc.util.Log.to;
import static raido.db.jooq.api_svc.tables.AppUser.APP_USER;
import static raido.db.jooq.api_svc.tables.ServicePoint.SERVICE_POINT;
import static raido.db.jooq.api_svc.tables.UserAuthzRequest.USER_AUTHZ_REQUEST;

@Component
public class AuthzRequestService {
  private static final Log log = to(AuthzRequestService.class);

  private DSLContext db;

  public AuthzRequestService(DSLContext db) {
    this.db = db;
  }

  public List<AuthzRequest> listAllRecentAuthzRequest() {
    return db.
      select(asterisk()).
      from(USER_AUTHZ_REQUEST).
        leftJoin(SERVICE_POINT).onKey(USER_AUTHZ_REQUEST.SERVICE_POINT_ID).
        leftJoin(APP_USER).onKey(USER_AUTHZ_REQUEST.RESPONDING_USER).
      orderBy(USER_AUTHZ_REQUEST.DATE_REQUESTED.desc()).
      limit(Constant.MAX_RETURN_RECORDS).
      fetch(this::mapJq2Rest);
  }
  
  public AuthzRequest readAuthzRequest(Long id) {
    return db.
      select(asterisk()).
      from(USER_AUTHZ_REQUEST).
        leftJoin(SERVICE_POINT).onKey(USER_AUTHZ_REQUEST.SERVICE_POINT_ID).
        leftJoin(APP_USER).onKey(USER_AUTHZ_REQUEST.RESPONDING_USER).
      where(USER_AUTHZ_REQUEST.ID.eq(id)).
      fetchSingle(this::mapJq2Rest);
  }
  
  public AuthzRequest mapJq2Rest(Record r) {
    return new AuthzRequest().
      id(r.get(USER_AUTHZ_REQUEST.ID)).
      status(mapJq2Rest(r.get(USER_AUTHZ_REQUEST.STATUS))).
      servicePointId(r.get(USER_AUTHZ_REQUEST.SERVICE_POINT_ID)).
      servicePointName(r.get(SERVICE_POINT.NAME)).
      comments(r.get(USER_AUTHZ_REQUEST.DESCRIPTION)).
      email(r.get(USER_AUTHZ_REQUEST.EMAIL)).
      clientId(r.get(USER_AUTHZ_REQUEST.CLIENT_ID)).
      idProvider(r.get(USER_AUTHZ_REQUEST.ID_PROVIDER).getLiteral()).
      subject(r.get(USER_AUTHZ_REQUEST.SUBJECT)).
      respondingUserId(r.get(USER_AUTHZ_REQUEST.RESPONDING_USER)).
      respondingUserEmail(r.get(APP_USER.EMAIL)).
      dateRequested(mapDateTime(r.get(USER_AUTHZ_REQUEST.DATE_REQUESTED))).
      dateResponded(mapDateTime(r.get(USER_AUTHZ_REQUEST.DATE_RESPONDED)));
  }

  // these simple mappings could/should be done as converters?
  public static OffsetDateTime mapDateTime(LocalDateTime ldt){
    if( ldt == null ){
      return null;
    }
    return ldt.atOffset(UTC);
  }

  public static AuthzRequestStatus mapJq2Rest(AuthRequestStatus status){
    return switch( status ){
      case APPROVED -> AuthzRequestStatus.APPROVED;
      case REQUESTED -> AuthzRequestStatus.REQUESTED;
      case REJECTED -> AuthzRequestStatus.REJECTED;
    };
  }  
}
