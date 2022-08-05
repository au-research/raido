package raido.apisvc.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class IoUtil {
  private static Log LOG = Log.to(IoUtil.class);

  /**
   "quietly" refers to not throwing exceptions, the exception will be
   logged as an error.
   */
  public static void closeQuietly(@Nullable Closeable is, @Nullable Log log) {
    if( is == null ){
      return;
    }

    try {
      is.close();
    }
    catch( Exception ex ){
      Log logger = log == null ? LOG : log;
      logger.errorEx("Ignore failure in closing the Closeable", ex);
    }
  }

  /**
   delegates to @(link {@link #closeQuietly(Closeable, Log)}
   */
  public static void closeQuietly(@Nullable Closeable is) {
    closeQuietly(is, null);
  }

  public static long copy(InputStream in, OutputStream out) {
    try {
      // Java 9, yay
      return in.transferTo(out);
    }
    catch( IOException e ){
      throw new RuntimeException(e);
    }
  }

  public static String linesAsString(InputStream input) {
    BufferedReader reader = new BufferedReader(
      new InputStreamReader(input, StandardCharsets.UTF_8));
    try {
      return reader.lines().collect(Collectors.joining("\n"));
    }
    finally {
      closeQuietly(reader);
    }
  }

}
