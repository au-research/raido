package au.org.raid.api.service.auth;

public enum RaidoClaim {
  /** IMPROVE: "isAuthorizedAppUser" claim should be changed to 
   "isApprovedUser", we only ever use this claim on the client-side to 
   differentiate the "approval" status of user during the sign-in flow.
   On the server side, we map the api-token to separate Authentication objects,
   either an ApiToken (i.e. ApprovedUserApiTokenPayload) or an 
   UnapprovedUserApiToken (payload). */
  IS_AUTHORIZED_APP_USER("isAuthorizedAppUser"),
  APP_USER_ID("appUserId"),
  SERVICE_POINT_ID("servicePointId"),
  CLIENT_ID("clientId"),
  EMAIL("email"),
  ROLE("role"),
  ;

  private String id;

  RaidoClaim(String id) {
    this.id = id;
  }

  /* IMPROVE:STO replace the various calls to `getId()` with util functions
  that work explicitly with claim and related objects, instead of strings. */
  public String getId() {
    return id;
  }
}
