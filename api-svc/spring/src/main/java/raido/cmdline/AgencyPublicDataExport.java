package raido.cmdline;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import raido.apisvc.service.export.AgencyPublicDataExportService;
import raido.apisvc.spring.config.environment.AgencyPublicDataExportProps;
import raido.apisvc.util.Log;
import raido.apisvc.util.Nullable;
import raido.cmdline.spring.config.CommandLineConfig;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;
import static raido.apisvc.util.ExceptionUtil.wrapException;
import static raido.apisvc.util.IdeUtil.formatClickable;
import static raido.apisvc.util.JvmUtil.normaliseJvmDefaults;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.infoLogExecutionTime;

public class AgencyPublicDataExport {
  private static final Log log = to(AgencyPublicDataExport.class);
  
  public static void main(String... args) throws Exception {
    normaliseJvmDefaults();
    export(null, null);
  }
  
  public static void export(
    @Nullable String filePath, 
    @Nullable Integer maxRows
  ){
    var ctx = configureSpring();
    var exportSvc = ctx.getBean(AgencyPublicDataExportService.class);
    var props = ctx.getBean(AgencyPublicDataExportProps.class);
    
    if( filePath == null ){
      filePath = props.allRaidsFilename;
    }
    
    log.with("file", formatClickable(Path.of(filePath))).
      info("starting export data process");

    try( var writer = newWriter(filePath) ){
      infoLogExecutionTime(log, "export data process", ()->
        exportSvc.exportData(writer, null, null, maxRows)
      );
    }
    catch( IOException e ){
      throw wrapException(e, "while opening writer");
    }
    
    log.info("finished export data process");
  }
  
  public static GenericApplicationContext configureSpring(){
    return new AnnotationConfigApplicationContext(
      CommandLineConfig.class );
  }

  public static BufferedWriter newWriter(String filePath) {
    try {
      return Files.newBufferedWriter(Path.of(filePath), UTF_8);
    }
    catch( IOException ex ){
      throw wrapException(ex, "while opening the writer");
    }
  }

}
