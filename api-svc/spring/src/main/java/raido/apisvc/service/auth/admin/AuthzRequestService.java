package raido.apisvc.service.auth.admin;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Component;
import raido.apisvc.endpoint.Constant;
import raido.apisvc.service.auth.AafOidc;
import raido.apisvc.service.auth.GoogleOidc;
import raido.apisvc.service.auth.NonAuthzTokenPayload;
import raido.apisvc.util.Log;
import raido.db.jooq.api_svc.enums.AuthRequestStatus;
import raido.db.jooq.api_svc.enums.IdProvider;
import raido.db.jooq.api_svc.enums.UserRole;
import raido.idl.raidv2.model.AuthzRequest;
import raido.idl.raidv2.model.AuthzRequestStatus;
import raido.idl.raidv2.model.UpdateAuthzRequest;
import raido.idl.raidv2.model.UpdateAuthzResponse;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static org.jooq.impl.DSL.asterisk;
import static org.jooq.impl.DSL.inline;
import static raido.apisvc.endpoint.raidv2.RaidoExperimental.RAIDO_SP_ID;
import static raido.apisvc.spring.security.IdProviderException.idpException;
import static raido.apisvc.util.Log.to;
import static raido.db.jooq.api_svc.enums.AuthRequestStatus.REQUESTED;
import static raido.db.jooq.api_svc.tables.AppUser.APP_USER;
import static raido.db.jooq.api_svc.tables.RaidoOperator.RAIDO_OPERATOR;
import static raido.db.jooq.api_svc.tables.ServicePoint.SERVICE_POINT;
import static raido.db.jooq.api_svc.tables.UserAuthzRequest.USER_AUTHZ_REQUEST;
import static raido.idl.raidv2.model.AuthzRequestStatus.APPROVED;

@Component
public class AuthzRequestService {
  private static final Log log = to(AuthzRequestService.class);

  private DSLContext db;
  private AafOidc aaf;
  private GoogleOidc google;


  public AuthzRequestService(DSLContext db, AafOidc aaf, GoogleOidc google) {
    this.db = db;
    this.aaf = aaf;
    this.google = google;
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

  public UpdateAuthzResponse updateRequestAuthz(
    NonAuthzTokenPayload user, UpdateAuthzRequest req
  ) {
    String email = user.getEmail().toLowerCase().trim();

    IdProvider idProvider = this.mapIdProvider(user.getClientId());
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

  /**
   Retuns the verified `id_token` from the IDP by calling the OIDC /token
   endpoint.
   */
  public DecodedJWT exchangeCodeForVerfiedJwt(
    String clientId, 
    String idpResponseCode
  ){
    var idProvider = mapIdProvider(clientId);
    DecodedJWT idProviderJwt;
    switch( idProvider ){
      case GOOGLE -> idProviderJwt = 
        google.exchangeCodeForVerifiedJwt(idpResponseCode);
      case AAF -> idProviderJwt = 
        aaf.exchangeCodeForVerifiedJwt(idpResponseCode);
      default -> throw idpException("unknown clientId %s", clientId);
    }
    
    return idProviderJwt;
  }

  public IdProvider mapIdProvider(String clientId){
    if( aaf.canHandle(clientId) ){
      return IdProvider.AAF;
    }
    else if( google.canHandle(clientId) ){
      return IdProvider.GOOGLE;
    }
    else {
      // improve should be a specific authn error?
      // and should it expose the error?
      throw idpException("unknown clientId %s", clientId);
    }
  }

  public boolean isRaidoOperator(String email) {
    return db.fetchExists(RAIDO_OPERATOR,
      RAIDO_OPERATOR.EMAIL.equalIgnoreCase(email)
    );
  }

}
