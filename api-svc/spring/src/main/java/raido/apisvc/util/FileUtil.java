package raido.apisvc.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {

  // https://stackoverflow.com/a/28315228/924597
  public static String resourceContent(String resourcePath){
    try {
      return new String(Files.readAllBytes(
        Paths.get(FileUtil.class.getResource(resourcePath).toURI()) ));
    }
    catch( IOException | URISyntaxException e ){
      throw new RuntimeException(e);
    }
  }

}