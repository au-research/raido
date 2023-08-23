package au.org.raid.api.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static java.nio.file.Files.readAllBytes;

public class FileUtil {

  // https://stackoverflow.com/a/28315228/924597
  public static String resourceContent(String resourcePath){
    Guard.hasValue(resourcePath);
    URL resource = FileUtil.class.getResource(resourcePath);
    Guard.notNull(()->"cant find " + resourcePath, resource);
    try {
      return new String(readAllBytes( Paths.get(resource.toURI()) ));
    }
    catch( IOException | URISyntaxException e ){
      throw new RuntimeException(e);
    }
  }

}