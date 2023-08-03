package raido.apisvc.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newInputStream;
import static raido.apisvc.util.ExceptionUtil.wrapException;
import static raido.apisvc.util.ExceptionUtil.wrapIoException;

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
      new InputStreamReader(input, UTF_8));
    try {
      return reader.lines().collect(Collectors.joining("\n"));
    }
    finally {
      closeQuietly(reader);
    }
  }

  public static Stream<String> lines(Path path) {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(
        new InputStreamReader(newInputStream(path), UTF_8) );
    }
    catch( IOException e ){
      throw wrapException(e, "while reading: " + path.toAbsolutePath());
    }
    
    return reader.lines();
  }

  /**
   Will create the dir to write to if it does not exist
   */
  public static BufferedWriter newWriter(String filePath) {
    Path path = Path.of(filePath);
    if( path.getParent() != null ){
      try {
        Files.createDirectories(path.getParent());
      }
      catch( IOException e ){
        throw wrapIoException(e, "could not create dir for path: %s", filePath);
      }
    }

    try {
      return Files.newBufferedWriter(path, UTF_8);
    }
    catch( IOException ex ){
      throw wrapIoException(ex, "while opening the writer");
    }
  }

  public static BufferedReader newReader(String filePath) {
    try {
      return Files.newBufferedReader(Path.of(filePath), UTF_8);
    }
    catch( IOException ex ){
      throw wrapIoException(ex, "while opening the reader");
    }
  }
}
