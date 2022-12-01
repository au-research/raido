package raido.apisvc.service.auth.admin;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Component;
import raido.apisvc.endpoint.Constant;
import raido.apisvc.spring.security.raidv2.AuthzTokenPayload;
import raido.apisvc.service.auth.NonAuthzTokenPayload;
import raido.apisvc.service.auth.RaidV2AppUserAuthService;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;
import raido.db.jooq.api_svc.enums.AuthRequestStatus;
import raido.db.jooq.api_svc.enums.IdProvider;
import raido.db.jooq.api_svc.enums.UserRole;
import raido.db.jooq.api_svc.tables.records.UserAuthzRequestRecord;
import raido.idl.raidv2.model.AppUser;
import raido.idl.raidv2.model.AuthzRequestExtraV1;
import raido.idl.raidv2.model.AuthzRequestStatus;
import raido.idl.raidv2.model.UpdateAuthzRequest;
import raido.idl.raidv2.model.UpdateAuthzRequestStatus;
import raido.idl.raidv2.model.UpdateAuthzResponse;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.jooq.impl.DSL.asterisk;
import static org.jooq.impl.DSL.inline;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;
import static raido.apisvc.service.auth.admin.AppUserService.mapRestRole2Jq;
import static raido.apisvc.util.ExceptionUtil.iae;
import static raido.apisvc.util.Log.to;
import static raido.db.jooq.api_svc.enums.AuthRequestStatus.REQUESTED;
import static raido.db.jooq.api_svc.enums.UserRole.OPERATOR;
import static raido.db.jooq.api_svc.tables.AppUser.APP_USER;
import static raido.db.jooq.api_svc.tables.RaidoOperator.RAIDO_OPERATOR;
import static raido.db.jooq.api_svc.tables.ServicePoint.SERVICE_POINT;
import static raido.db.jooq.api_svc.tables.UserAuthzRequest.USER_AUTHZ_REQUEST;
import static raido.idl.raidv2.model.AuthzRequestStatus.APPROVED;

/**
 "Authorization requests" - a new user creates an AuthzRequest to be allowed to
 use the system.  When an AuthzRequest is approved by an admin, an "AppUser" is
 created and the user can sign in and start using the system.
 */
@Component
public class AuthzRequestService {
  private static final Log log = to(AuthzRequestService.class);

  private DSLContext db;
  private RaidV2AppUserAuthService userAuthSvc;


  public AuthzRequestService(
    DSLContext db, 
    RaidV2AppUserAuthService userAuthSvc
  ) {
    this.db = db;
    this.userAuthSvc = userAuthSvc;
  }

  public List<AuthzRequestExtraV1> listAllRecentAuthzRequest() {
    return db.
      select(asterisk()).
      from(USER_AUTHZ_REQUEST).
        leftJoin(SERVICE_POINT).onKey(USER_AUTHZ_REQUEST.SERVICE_POINT_ID).
        leftJoin(APP_USER).onKey(USER_AUTHZ_REQUEST.RESPONDING_USER).
      orderBy(USER_AUTHZ_REQUEST.DATE_REQUESTED.desc()).
      limit(Constant.MAX_EXPERIMENTAL_RECORDS).
      fetch(this::mapAuthzRequest);
  }
  
  public AuthzRequestExtraV1 readAuthzRequest(Long authzRequestId) {
    return db.
      select(asterisk()).
      from(USER_AUTHZ_REQUEST).
        leftJoin(SERVICE_POINT).onKey(USER_AUTHZ_REQUEST.SERVICE_POINT_ID).
        leftJoin(APP_USER).onKey(USER_AUTHZ_REQUEST.RESPONDING_USER).
      where(USER_AUTHZ_REQUEST.ID.eq(authzRequestId)).
      fetchSingle(this::mapAuthzRequest);
  }
  
  public Optional<AuthzRequestExtraV1> readAuthzRequestForUser(
    AppUser appUser
  ) {
    return db.
      select(asterisk()).
      from(USER_AUTHZ_REQUEST).
      leftJoin(SERVICE_POINT).onKey(USER_AUTHZ_REQUEST.SERVICE_POINT_ID).
      leftJoin(APP_USER).onKey(USER_AUTHZ_REQUEST.RESPONDING_USER).
      where(
        USER_AUTHZ_REQUEST.APPROVED_USER.eq(appUser.getId()).
          and(
            USER_AUTHZ_REQUEST.STATUS.eq(AuthRequestStatus.APPROVED)).
          and(
            USER_AUTHZ_REQUEST.SERVICE_POINT_ID.eq(appUser.getServicePointId())
          )
      ).
      orderBy(USER_AUTHZ_REQUEST.DATE_RESPONDED.desc()).
      limit(1).fetchOptional(this::mapAuthzRequest);
  }
  
  public AuthzRequestExtraV1 mapAuthzRequest(Record r) {
    return new AuthzRequestExtraV1().
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

  public static AuthRequestStatus mapRest2Jq(AuthzRequestStatus status){
    return switch( status ){
      case APPROVED -> AuthRequestStatus.APPROVED;
      case REQUESTED -> AuthRequestStatus.REQUESTED;
      case REJECTED -> AuthRequestStatus.REJECTED;
    };
  }

  public UpdateAuthzResponse updateRequestAuthz(
    NonAuthzTokenPayload user, UpdateAuthzRequest req
  ) {
    String email = user.getEmail().toLowerCase().trim();

    IdProvider idProvider = userAuthSvc.mapIdProvider(user.getClientId());
    if(
      // we only promote raido SP users to operator
      req.getServicePointId() == RAIDO_SP_ID &&
        /* this is probably temporary, helps me test easily, no specific need
        to restrict it. */
        idProvider == IdProvider.GOOGLE &&
        isRaidoOperator(email)
    ){
      /* this will fail if operator already exists as a user of a different SP 
      I don't want to add the complexity to deal with that right now.*/
      log.with("user", user).info("adding raido operator");
      db.insertInto(APP_USER).
        set(APP_USER.SERVICE_POINT_ID, req.getServicePointId()).
        set(APP_USER.EMAIL, email).
        set(APP_USER.CLIENT_ID, user.getClientId()).
        set(APP_USER.ID_PROVIDER, idProvider).
        set(APP_USER.SUBJECT, user.getSubject()).
        set(APP_USER.ROLE, UserRole.OPERATOR).
        returningResult(APP_USER.ID).
        fetchOne();

      /* client shouldn't need the user id, should re-fresh and re-auth to 
      id-provider, and the new token returned from /idpresponse will be good 
      to use. */
      return new UpdateAuthzResponse().status(APPROVED);
    }

    db.insertInto(USER_AUTHZ_REQUEST).
      set(USER_AUTHZ_REQUEST.SERVICE_POINT_ID, req.getServicePointId()).
      set(USER_AUTHZ_REQUEST.STATUS, REQUESTED).
      set(USER_AUTHZ_REQUEST.EMAIL, email).
      set(USER_AUTHZ_REQUEST.CLIENT_ID, user.getClientId()).
      set(USER_AUTHZ_REQUEST.ID_PROVIDER, idProvider).
      set(USER_AUTHZ_REQUEST.SUBJECT, user.getSubject()).
      set(USER_AUTHZ_REQUEST.DESCRIPTION, req.getComments()).
      onConflict(
        USER_AUTHZ_REQUEST.SERVICE_POINT_ID,
        USER_AUTHZ_REQUEST.CLIENT_ID,
        USER_AUTHZ_REQUEST.SUBJECT ).
      // inline needed because: https://stackoverflow.com/a/73782610/924597
        where( USER_AUTHZ_REQUEST.STATUS.eq(
        inline(REQUESTED, USER_AUTHZ_REQUEST.STATUS)) ).
      doUpdate().
      set(USER_AUTHZ_REQUEST.DESCRIPTION, req.getComments()).
      set(USER_AUTHZ_REQUEST.DATE_REQUESTED, LocalDateTime.now()).
      execute();
    return new UpdateAuthzResponse().status(AuthzRequestStatus.REQUESTED);
  }

  public boolean isRaidoOperator(String email) {
    return db.fetchExists(RAIDO_OPERATOR,
      RAIDO_OPERATOR.EMAIL.equalIgnoreCase(email)
    );
  }

  public void updateAuthzRequestStatus(
    AuthzTokenPayload respondingUser,
    UpdateAuthzRequestStatus req,
    UserAuthzRequestRecord authzRecord
  ) {
    if( req.getStatus() == APPROVED ){
      Guard.isTrue(authzRecord.getStatus() == REQUESTED);

      var approvedRole = mapRestRole2Jq(req.getRole());
      if( approvedRole == OPERATOR ){
        /* operators don't get "approved". they get auto-approved, or manually
        promoted via the edit user screen. */
        var iae = iae("approved role can not be operator");
        log.with("respondingUser", respondingUser).
          with("authzRequestId", req.getAuthzRequestId()).
          error(iae.getMessage());
        throw iae;
      }

      var approvedUser = db.insertInto(APP_USER).
        set(APP_USER.SERVICE_POINT_ID, authzRecord.getServicePointId()).
        set(APP_USER.EMAIL, authzRecord.getEmail()).
        set(APP_USER.CLIENT_ID, authzRecord.getClientId()).
        set(APP_USER.SUBJECT, authzRecord.getSubject()).
        set(APP_USER.ID_PROVIDER, authzRecord.getIdProvider()).
        set(APP_USER.ROLE, approvedRole).
        onConflict(APP_USER.EMAIL, APP_USER.CLIENT_ID, APP_USER.SUBJECT).
        where(APP_USER.ENABLED.eq(true)).
        doUpdate().
        set(APP_USER.SERVICE_POINT_ID, authzRecord.getServicePointId()).
        returning(APP_USER.ID).
        fetchSingle();

      authzRecord.setStatus(AuthRequestStatus.APPROVED);
      authzRecord.setApprovedUser(approvedUser.getId());
      authzRecord.setRespondingUser(respondingUser.getAppUserId());
      authzRecord.setDateResponded(LocalDateTime.now());
      authzRecord.update();
      
    }
    else if( req.getStatus() == AuthzRequestStatus.REJECTED ){
      authzRecord.setStatus(AuthRequestStatus.REJECTED);
      authzRecord.setRespondingUser(respondingUser.getAppUserId());
      authzRecord.setDateResponded(LocalDateTime.now());
      authzRecord.update();
    }
    else {
      var iae = iae("invalid status update value");
      log.with("user", respondingUser).
        with("request", req).
        with("db.status", authzRecord.getStatus()).
        error(iae.getMessage());
      throw iae;
    }
  }
}
