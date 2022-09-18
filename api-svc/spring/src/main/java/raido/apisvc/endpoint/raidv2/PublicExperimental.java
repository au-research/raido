package raido.apisvc.endpoint.raidv2;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.endpoint.auth.AuthnEndpoint;
import raido.apisvc.spring.StartupListener;
import raido.apisvc.spring.bean.AppInfoBean;
import raido.apisvc.util.Log;
import raido.db.jooq.api_svc.enums.IdProvider;
import raido.db.jooq.api_svc.enums.UserRole;
import raido.idl.raidv2.api.PublicExperimentalApi;
import raido.idl.raidv2.model.PublicServicePoint;
import raido.idl.raidv2.model.UpdateAuthzRequest;
import raido.idl.raidv2.model.UpdateAuthzResponse;
import raido.idl.raidv2.model.VersionResult;

import java.util.List;

import static java.time.ZoneOffset.UTC;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.getNonAuthzPayload;
import static raido.apisvc.endpoint.raidv2.RaidoExperimental.RAIDO_SP_ID;
import static raido.apisvc.util.Log.to;
import static raido.db.jooq.api_svc.enums.AuthRequestStatus.REQUESTED;
import static raido.db.jooq.api_svc.tables.AppUser.APP_USER;
import static raido.db.jooq.api_svc.tables.RaidoOperator.RAIDO_OPERATOR;
import static raido.db.jooq.api_svc.tables.ServicePoint.SERVICE_POINT;
import static raido.db.jooq.api_svc.tables.UserAuthzRequest.USER_AUTHZ_REQUEST;
import static raido.idl.raidv2.model.UpdateAuthzResponse.StatusEnum;

@Scope(proxyMode = TARGET_CLASS)
@RestController
@Transactional
public class PublicExperimental implements PublicExperimentalApi {
  private static final Log log = to(PublicExperimental.class);
  
  private DSLContext db;
  private AppInfoBean appInfo;
  private StartupListener startup;
  private AuthnEndpoint authn;

  public PublicExperimental(DSLContext db,
    AppInfoBean appInfo,
    StartupListener startup,
    AuthnEndpoint authn
  ) {
    this.db = db;
    this.appInfo = appInfo;
    this.startup = startup;
    this.authn = authn;
  }

  @Override
  public VersionResult version() {
    return new VersionResult().
      buildVersion(appInfo.getBuildVersion()).
      buildCommitId(appInfo.getBuildVersion()).
      buildDate(appInfo.getBuildDate()).
      startDate(startup.getStartTime().atOffset(UTC));
 
  }

  @Override
  public List<PublicServicePoint> listPublicServicePoint() {
    return db.
      select(
        SERVICE_POINT.ID,
        SERVICE_POINT.NAME).
      from(SERVICE_POINT).
      where(SERVICE_POINT.DISABLED.isFalse()).
      fetchInto(PublicServicePoint.class);
  }


  public boolean isRaidoOperator(String email) {
    return db.fetchExists(RAIDO_OPERATOR,
      RAIDO_OPERATOR.EMAIL.equalIgnoreCase(email)
    );
  }

  @Override
  public UpdateAuthzResponse updateRequestAuthz(UpdateAuthzRequest req) {
    var user = getNonAuthzPayload();

    // TODO:STO validate the request data

    String email = user.getEmail().toLowerCase().trim();

    
    IdProvider idProvider = authn.mapIdProvider(user.getClientId());
    if(
      // we only promote raido SP users to operator
      req.getServicePointId() == RAIDO_SP_ID &&
        /* this is probably temporary, helps me test easily, no real need 
        * Google because AAF doesn't work locally. */
      idProvider == IdProvider.GOOGLE && 
      isRaidoOperator(email)
    ){
      /* this will fail if operator already exists as a user of a different SP 
      I don't want to add the complexity to deal with that right now.*/
      log.with("user", user).info("adding raido operator");
      var userId = db.insertInto(APP_USER).
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
      return new UpdateAuthzResponse().status(StatusEnum.APPROVED);
    }

    db.insertInto(USER_AUTHZ_REQUEST).
      set(USER_AUTHZ_REQUEST.SERICE_POINT_ID, req.getServicePointId()).
      set(USER_AUTHZ_REQUEST.STATUS, REQUESTED).
      set(USER_AUTHZ_REQUEST.EMAIL, email).
      set(USER_AUTHZ_REQUEST.CLIENT_ID, user.getClientId()).
      set(USER_AUTHZ_REQUEST.ID_PROVIDER,
        idProvider).
      set(USER_AUTHZ_REQUEST.SUBJECT, user.getSubject()).
      set(USER_AUTHZ_REQUEST.DESCRIPTION, req.getComments()).
      execute();
    return new UpdateAuthzResponse().status(StatusEnum.REQUESTED);
  }

}
