package raido.cmdline;

import au.org.raid.api.service.export.AgencyPublicDataExportService;
import au.org.raid.api.spring.config.environment.AgencyPublicDataExportProps;
import au.org.raid.api.util.Log;
import au.org.raid.api.util.Nullable;

import java.io.IOException;
import java.nio.file.Path;

import static au.org.raid.api.util.ExceptionUtil.wrapException;
import static au.org.raid.api.util.IdeUtil.formatClickable;
import static au.org.raid.api.util.IoUtil.newWriter;
import static au.org.raid.api.util.JvmUtil.normaliseJvmDefaults;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.ObjectUtil.infoLogExecutionTime;
import static raido.cmdline.spring.config.CommandLineConfig.configureSpring;

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

}
