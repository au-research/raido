package au.org.raid.api.service.auth.admin;

import au.org.raid.api.endpoint.Constant;
import au.org.raid.api.service.auth.RaidV2AppUserOidcService;
import au.org.raid.api.spring.security.raidv2.ApiToken;
import au.org.raid.api.util.Guard;
import au.org.raid.api.util.Log;
import au.org.raid.db.jooq.api_svc.enums.AuthRequestStatus;
import au.org.raid.db.jooq.api_svc.enums.IdProvider;
import au.org.raid.db.jooq.api_svc.enums.UserRole;
import au.org.raid.db.jooq.api_svc.tables.records.UserAuthzRequestRecord;
import au.org.raid.idl.raidv2.model.*;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static au.org.raid.api.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;
import static au.org.raid.api.service.auth.admin.AppUserService.mapRestRole2Jq;
import static au.org.raid.api.util.ExceptionUtil.iae;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.db.jooq.api_svc.enums.AuthRequestStatus.REQUESTED;
import static au.org.raid.db.jooq.api_svc.enums.UserRole.OPERATOR;
import static au.org.raid.db.jooq.api_svc.tables.AppUser.APP_USER;
import static au.org.raid.db.jooq.api_svc.tables.RaidoOperator.RAIDO_OPERATOR;
import static au.org.raid.db.jooq.api_svc.tables.ServicePoint.SERVICE_POINT;
import static au.org.raid.db.jooq.api_svc.tables.UserAuthzRequest.USER_AUTHZ_REQUEST;
import static au.org.raid.idl.raidv2.model.AuthzRequestStatus.APPROVED;
import static java.time.ZoneOffset.UTC;
import static org.jooq.impl.DSL.inline;

/**
 "Authorization requests" - a new user creates an AuthzRequest to be allowed to
 use the system.  When an AuthzRequest is approved by an admin, an "AppUser" is
 created and the user can sign in and start using the system.
 */
@Component
public class AuthzRequestService {
  private static final Log log = to(AuthzRequestService.class);

  private DSLContext db;
  private RaidV2AppUserOidcService userAuthSvc;


  public AuthzRequestService(
    DSLContext db, 
    RaidV2AppUserOidcService userAuthSvc
  ) {
    this.db = db;
    this.userAuthSvc = userAuthSvc;
  }

  public List<AuthzRequestExtraV1> listAllRecentAuthzRequest() {
    return db.select().
      from(USER_AUTHZ_REQUEST).
        leftJoin(SERVICE_POINT).onKey(USER_AUTHZ_REQUEST.SERVICE_POINT_ID).
        leftJoin(APP_USER).onKey(USER_AUTHZ_REQUEST.RESPONDING_USER).
      orderBy(USER_AUTHZ_REQUEST.DATE_REQUESTED.desc()).
      limit(Constant.MAX_EXPERIMENTAL_RECORDS).
      fetch(this::mapAuthzRequest);
  }
  
  public AuthzRequestExtraV1 readAuthzRequest(Long authzRequestId) {
    return db.select().
      from(USER_AUTHZ_REQUEST).
        leftJoin(SERVICE_POINT).onKey(USER_AUTHZ_REQUEST.SERVICE_POINT_ID).
        leftJoin(APP_USER).onKey(USER_AUTHZ_REQUEST.RESPONDING_USER).
      where(USER_AUTHZ_REQUEST.ID.eq(authzRequestId)).
      fetchSingle(this::mapAuthzRequest);
  }
  
  public Optional<AuthzRequestExtraV1> readAuthzRequestForUser(
    AppUser appUser
  ) {
    return db.select().
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
    String clientId,
    String email,
    String subject,
    UpdateAuthzRequest req
  ) {
    Guard.allHaveValue("must have values", clientId, email, subject);
    
    email = email.toLowerCase().trim();
    
    IdProvider idProvider = userAuthSvc.mapIdProvider(clientId);
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
      log.with("clientId", clientId).
        with("subject", subject).
        with("email", email).
        info("adding raido operator");
      db.insertInto(APP_USER).
        set(APP_USER.SERVICE_POINT_ID, req.getServicePointId()).
        set(APP_USER.EMAIL, email).
        set(APP_USER.CLIENT_ID, clientId).
        set(APP_USER.ID_PROVIDER, idProvider).
        set(APP_USER.SUBJECT, subject).
        set(APP_USER.ROLE, UserRole.OPERATOR).
        returningResult(APP_USER.ID).
        fetchOne();

      /* client shouldn't need the user id, should re-fresh and re-auth to 
      id-provider, and the new token returned from /idpresponse will be good 
      to use. */
      return new UpdateAuthzResponse().status(APPROVED);
    }

    var id = db.insertInto(USER_AUTHZ_REQUEST).
      set(USER_AUTHZ_REQUEST.SERVICE_POINT_ID, req.getServicePointId()).
      set(USER_AUTHZ_REQUEST.STATUS, REQUESTED).
      set(USER_AUTHZ_REQUEST.EMAIL, email).
      set(USER_AUTHZ_REQUEST.CLIENT_ID, clientId).
      set(USER_AUTHZ_REQUEST.ID_PROVIDER, idProvider).
      set(USER_AUTHZ_REQUEST.SUBJECT, subject).
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
      returningResult(USER_AUTHZ_REQUEST.ID).
      fetchOneInto(Long.class);
    return new UpdateAuthzResponse().
      status(AuthzRequestStatus.REQUESTED).
      authzRequestId(id);
  }

  public boolean isRaidoOperator(String email) {
    return db.fetchExists(RAIDO_OPERATOR,
      RAIDO_OPERATOR.EMAIL.equalIgnoreCase(email)
    );
  }

  public void updateAuthzRequestStatus(
    ApiToken respondingUser,
    UpdateAuthzRequestStatus req,
    UserAuthzRequestRecord authzRecord
  ) {
    if( req.getStatus() == APPROVED ){
      Guard.isTrue(authzRecord.getStatus() == REQUESTED);
      Guard.hasValue("must provide role", req.getRole());

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
