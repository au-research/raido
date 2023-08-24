package au.org.raid.api.service.auth;

import java.util.StringJoiner;

import static au.org.raid.api.util.StringUtil.mask;

/**
 * The toString() of this method will mask sensitive values, be careful to
 * maintain that functionality or we'll end up with sensitive values in log
 * files.
 * IMPROVE: refactor `toString()` like "logSafeValues()"
 * to make it obvious what's going on.
 */
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
