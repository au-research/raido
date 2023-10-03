package au.org.raid.api.util;

import java.util.regex.Pattern;

import static au.org.raid.api.util.ExceptionUtil.illegalArgException;
import static au.org.raid.api.util.Log.to;

public class Security {
    private static final Log log = to(Security.class);

    /**
     * SSRF prevention - see ssrf.md
     */
    public static void guardSsrf(String name, Pattern regex, String pidUrl) {
        if (regex.matcher(pidUrl).matches()) {
            return;
        }

        log.with(name, pidUrl).warn("failed SSRF prevention");
        throw illegalArgException(name + "failed SSRF prevention");
    }
}
