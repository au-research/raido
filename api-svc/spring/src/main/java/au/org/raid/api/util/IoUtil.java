package au.org.raid.api.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newInputStream;

@Slf4j
public class IoUtil {
    public static void closeQuietly(@Nullable Closeable is, @Nullable Log log) {
        if (is == null) {
            return;
        }

        try {
            is.close();
        } catch (Exception ex) {
            log.error("Ignore failure in closing the Closeable", ex);
        }
    }

    /**
     * delegates to @(link {@link #closeQuietly(Closeable, Log)}
     */
    public static void closeQuietly(@Nullable Closeable is) {
        closeQuietly(is, null);
    }

    public static long copy(InputStream in, OutputStream out) {
        try {
            // Java 9, yay
            return in.transferTo(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String linesAsString(InputStream input) {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(input, UTF_8));
        try {
            return reader.lines().collect(Collectors.joining("\n"));
        } finally {
            closeQuietly(reader);
        }
    }

    public static Stream<String> lines(Path path) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(newInputStream(path), UTF_8));
        } catch (IOException e) {
            throw ExceptionUtil.wrapException(e, "while reading: " + path.toAbsolutePath());
        }

        return reader.lines();
    }

    /**
     * Will create the dir to write to if it does not exist
     */
    public static BufferedWriter newWriter(String filePath) {
        Path path = Path.of(filePath);
        if (path.getParent() != null) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                throw ExceptionUtil.wrapIoException(e, "could not create dir for path: %s", filePath);
            }
        }

        try {
            return Files.newBufferedWriter(path, UTF_8);
        } catch (IOException ex) {
            throw ExceptionUtil.wrapIoException(ex, "while opening the writer");
        }
    }

    public static BufferedReader newReader(String filePath) {
        try {
            return Files.newBufferedReader(Path.of(filePath), UTF_8);
        } catch (IOException ex) {
            throw ExceptionUtil.wrapIoException(ex, "while opening the reader");
        }
    }
}
