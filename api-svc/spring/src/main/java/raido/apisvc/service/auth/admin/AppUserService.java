package raido.apisvc.service.auth.admin;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;
import raido.apisvc.service.auth.AuthzTokenPayload;
import raido.apisvc.util.DateUtil;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;
import raido.db.jooq.api_svc.enums.IdProvider;
import raido.db.jooq.api_svc.enums.UserRole;
import raido.db.jooq.api_svc.tables.records.AppUserRecord;
import raido.idl.raidv2.model.ApiKey;
import raido.idl.raidv2.model.AppUser;

import java.time.LocalDateTime;
import java.util.List;

import static raido.apisvc.endpoint.Constant.MAX_EXPERIMENTAL_RECORDS;
import static raido.apisvc.util.DateUtil.offset2Local;
import static raido.apisvc.util.ExceptionUtil.iae;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.areEqual;
import static raido.db.jooq.api_svc.enums.UserRole.OPERATOR;
import static raido.db.jooq.api_svc.enums.UserRole.SP_ADMIN;
import static raido.db.jooq.api_svc.enums.UserRole.SP_USER;
import static raido.db.jooq.api_svc.tables.AppUser.APP_USER;

@Component
public class AppUserService {
  private static final Log log = to(AppUserService.class);

  private DSLContext db;

  public AppUserService(DSLContext db) {
    this.db = db;
  }

  public void updateAppUser(
    AppUser req,
    AuthzTokenPayload invokingUser,
    AppUserRecord targetUser
  ) {
    Guard.isTrue("RAIDO_API values should use the api-key endpoints", 
      !areEqual(req.getIdProvider(), IdProvider.RAIDO_API.getLiteral()) );
    // the role of the person doing the action
    UserRole invokingRole = mapRestRole2Jq(invokingUser.getRole());
    // the current role of the user being updated from
    UserRole currentRole = targetUser.getRole();
    // the new role of the user being update to 
    UserRole targetRole = mapRestRole2Jq(req.getRole());

    if( currentRole == OPERATOR && targetRole != OPERATOR ){
      if( invokingRole != OPERATOR ){
        var iae = iae("only an OPERATOR can demote an OPERATOR");
        log.with("invokingUser", invokingUser).
          with("targetUserId", req.getId()).
          with("targetUserEmail", req.getEmail()).
          with("targetRole", targetRole.getLiteral()).
          with("currentRole", currentRole.getLiteral()).
          error(iae.getMessage());
        throw iae;
      }
    }
    else if( currentRole != OPERATOR && targetRole == OPERATOR ){
      if( invokingRole != OPERATOR ){
        var iae = iae("only an OPERATOR can promote an OPERATOR");
        log.with("invokingUser", invokingUser).
          with("targetUserId", req.getId()).
          with("targetUserEmail", req.getEmail()).
          with("targetRole", targetRole.getLiteral()).
          with("currentRole", currentRole.getLiteral()).
          error(iae.getMessage());
        throw iae;
      }
    }
    
    /* at this point, we've check that the invokingUser is OP or ADMIN, and 
     that they're for the associated SP if ADMIN, and that only an OP is 
     dealing with OP stuff.  
     Should be good to just set the role. */
    targetUser.setRole(targetRole);

    targetUser.setEnabled(req.getEnabled());
    targetUser.setTokenCutoff(offset2Local(req.getTokenCutoff()));

    targetUser.update();
  }

  private UserRole mapRestRole2Jq(String role) {
    if( areEqual(OPERATOR.getLiteral(), role) ){
      return OPERATOR;
    }
    else if( areEqual(SP_ADMIN.getLiteral(), role) ){
      return SP_ADMIN;
    }
    else if( areEqual(SP_USER.getLiteral(), role) ){
      return SP_USER;
    }
    else {
      throw iae("could not map role: %s", role);
    }
  }


  public List<AppUser> listAppUser(
    Long servicePointId
  ) {
    return db.select().
      from(APP_USER).
      where(
        APP_USER.SERVICE_POINT_ID.eq(servicePointId).
          and( APP_USER.ID_PROVIDER.ne(IdProvider.RAIDO_API) )).
      orderBy(APP_USER.EMAIL.asc()).
      limit(MAX_EXPERIMENTAL_RECORDS).
      fetchInto(AppUser.class);
  }
  
  public List<ApiKey> listApiKey(
    Long servicePointId
  ) {
    return db.select().
      from(APP_USER).
      where(
        APP_USER.SERVICE_POINT_ID.eq(servicePointId).
          and( APP_USER.ID_PROVIDER.eq(IdProvider.RAIDO_API) )).
      orderBy(APP_USER.EMAIL.asc()).
      limit(MAX_EXPERIMENTAL_RECORDS).
      fetchInto(ApiKey.class);
  }

  public long updateApiKey(
    ApiKey req,
    AuthzTokenPayload invokingUser
  ) {
    Guard.isTrue("API key idProvider can only be RAIDO_API", 
      areEqual(req.getIdProvider(), IdProvider.RAIDO_API.getLiteral()) );
    
    var record = req.getId() == null ?
      db.newRecord(APP_USER) :
      db.fetchSingle(APP_USER, APP_USER.ID.eq(req.getId()));
    
    // can only be set at create-time
    if( req.getId() == null ){
      record.setServicePointId(req.getServicePointId());
      record.setEmail(req.getSubject());
      record.setClientId("");
      record.setIdProvider(IdProvider.RAIDO_API);
      record.setDateCreated(LocalDateTime.now());
    }

    UserRole targetRole = mapRestRole2Jq(req.getRole());
    if( targetRole != SP_USER && targetRole != SP_ADMIN ){
      var iae = iae("api-key can only be SP_USER or SP_ADMIN role");
      log.with("invokingUser", invokingUser).
        with("targetAppUserId", req.getId()).
        with("targetSubject", req.getSubject()).
        with("targetRole", targetRole.getLiteral()).
        error(iae.getMessage());
      throw iae;
    }
    
    // allowing subject to be updated, not sure this is a great plan
    record.setSubject(req.getSubject());
    record.setRole(targetRole);
    record.setEnabled(req.getEnabled());
    record.setTokenCutoff(DateUtil.offset2Local(req.getTokenCutoff()));

    if( req.getId() == null ){
      record.insert();
    }
    else {
      record.setId(req.getId());
      record.update();
    }
    
    return record.getId();
  }
  
}
