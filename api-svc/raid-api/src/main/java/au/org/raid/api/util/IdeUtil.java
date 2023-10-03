package au.org.raid.api.util;

import java.nio.file.Path;

public class IdeUtil {
    public static String formatClickable(Path path) {
    /* normalize paths because some tools (e.g. windows explorer) will fail
    if given paths of the form "dir/./file.txt" or "dir/../file.text", etc. */
        if (JvmUtil.isWindowsPlatform()) {
            return "file:///" + path.toAbsolutePath().normalize().
                    toString().replace("\\", "/");
        }

        return "file://" + path.toAbsolutePath().normalize();
    }

    public static String formatClickable(String path) {
        return formatClickable(Path.of(path));
    }
}
