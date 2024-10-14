package au.org.raid.api.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collections;
import java.util.List;

public class TokenUtil {
    private static final String SUBJECT_CLAIM = "sub";
    private static final String USER_RAIDS_CLAIM = "user_raids";
    private static final String ADMIN_RAIDS_CLAIM = "admin_raids";

    public static Jwt getToken() {
        return ((JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getToken();
    }

    public static String getUserId() {
        return (String) getToken().getClaims().get(SUBJECT_CLAIM);
    }

    public static List<String> getUserRaids() {
        if (getToken().getClaims().get(USER_RAIDS_CLAIM) != null) {
            return ((List<?>) getToken().getClaims().get(USER_RAIDS_CLAIM)).stream()
                    .filter(handle -> handle instanceof String)
                    .map(handle -> (String) handle)
                    .toList();
        }
        return Collections.emptyList();
    }

    public static List<String> getAdminRaids() {
        if (getToken().getClaims().get(ADMIN_RAIDS_CLAIM) != null) {
            return ((List<?>) getToken().getClaims().get(ADMIN_RAIDS_CLAIM)).stream()
                    .filter(handle -> handle instanceof String)
                    .map(handle -> (String) handle)
                    .toList();
        }
        return Collections.emptyList();
    }
}
