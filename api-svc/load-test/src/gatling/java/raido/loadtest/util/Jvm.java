package raido.loadtest.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.getProperty;

public class Jvm {
  
  public static Path cwd(){
    return Paths.get(System.getProperty("user.dir"));
  }
  
  public static List<String> classpath(){
    return Arrays.stream(getProperty("java.class.path").
      split(getProperty("path.separator"))).toList();
  }
 }
