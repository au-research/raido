package raido.apisvc.service.auth;

import java.util.StringJoiner;

import static raido.apisvc.util.StringUtil.mask;

public class OAuthTokenResponse {
  public String access_token;
  public int expires_in;
  public String scope;
  public String token_type;
  public String id_token;

  @Override
  public String toString() {
    return new StringJoiner(
      ", ",
      OAuthTokenResponse.class.getSimpleName() + "[",
      "]")
      .add("access_token='" + mask(access_token) + "'")
      .add("expires_in=" + expires_in)
      .add("scope='" + scope + "'")
      .add("token_type='" + token_type + "'")
      .add("id_token='" + mask(id_token) + "'")
      .toString();
  }
}
