package raido.apisvc.util;

import java.nio.file.Path;

import static raido.apisvc.util.JvmUtil.isWindowsPlatform;

public class IdeUtil {
  public static String formatClickable(Path path){
    if( isWindowsPlatform() ){
      return "file:///" + path.toAbsolutePath().toString().replace("\\", "/");
    }
    
    return "file://" + path.toAbsolutePath();
  }
  
  public static String formatClickable(String path){
    return formatClickable(Path.of(path));
  }
}
