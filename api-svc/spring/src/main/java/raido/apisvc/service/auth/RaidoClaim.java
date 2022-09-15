package raido.apisvc.service.auth;

public enum RaidoClaim {
  IS_AUTHORIZED_APP_USER("isAuthorizedAppUser"),
  APP_USER_ID("appUserId"),
  CLIENT_ID("clientId"),
  EMAIL("email"),
  ROLE("role"),
  ;

  private String id;

  RaidoClaim(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }
}
