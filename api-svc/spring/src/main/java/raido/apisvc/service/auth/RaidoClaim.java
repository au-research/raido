package raido.apisvc.service.auth;

public enum RaidoClaim {
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
