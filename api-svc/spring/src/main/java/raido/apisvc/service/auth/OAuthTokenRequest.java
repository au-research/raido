package raido.apisvc.service.auth;

import java.util.StringJoiner;

import static raido.apisvc.util.StringUtil.mask;

public class OAuthTokenRequest {
  public String code;
  public String client_id;
  public String client_secret;
  public String grant_type;
  public String redirect_uri;

  public OAuthTokenRequest code(String code) {
    this.code = code;
    return this;
  }

  public OAuthTokenRequest client_id(String client_id) {
    this.client_id = client_id;
    return this;
  }

  public OAuthTokenRequest client_secret(String client_secret) {
    this.client_secret = client_secret;
    return this;
  }

  public OAuthTokenRequest grant_type(String grant_type) {
    this.grant_type = grant_type;
    return this;
  }

  public OAuthTokenRequest redirect_uri(String redirect_uri) {
    this.redirect_uri = redirect_uri;
    return this;
  }

  @Override
  public String toString() {
    return new StringJoiner(
      ", ",
      OAuthTokenRequest.class.getSimpleName() + "[",
      "]")
      .add("code='" + mask(code) + "'")
      .add("client_id='" + client_id + "'")
      .add("client_secret='" + mask(client_secret) + "'")
      .add("grant_type='" + grant_type + "'")
      .add("redirect_uri='" + redirect_uri + "'")
      .toString();
  }
}
