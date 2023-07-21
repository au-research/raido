package raido.cmdline;

import raido.apisvc.service.export.BuildSearchIndexService;
import raido.apisvc.spring.config.environment.BuildSearchIndexProps;
import raido.apisvc.util.Log;

import static raido.apisvc.util.ExceptionUtil.wrapException;
import static raido.apisvc.util.IdeUtil.formatClickable;
import static raido.apisvc.util.JvmUtil.normaliseJvmDefaults;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.infoLogExecutionTime;
import static raido.cmdline.spring.config.CommandLineConfig.configureSpring;
import static raido.cmdline.spring.config.CommandLineConfig.newReader;

/**
 This is intended to be run for each reg-agency, path values are defaulted for
 raido but those will be overridden for other reg-agencies.
 */
public class BuildSearchIndex {
  private static final Log log = to(BuildSearchIndex.class);
  
  public static void main(String... args) throws Exception {
    normaliseJvmDefaults();
    buildIndex();
  }
  
  public static void buildIndex(){
    var ctx = configureSpring();
    var indexSvc = ctx.getBean(BuildSearchIndexService.class);
    var props = ctx.getBean(BuildSearchIndexProps.class);
    
    var publicExportPath = props.agencyPublicExportPath;
    
    log.with("publicExportPath", formatClickable(publicExportPath)).
      info("starting index build process");

    try(
      var reader = newReader(publicExportPath);
    ) {
      infoLogExecutionTime(log, 
        "building index for %s".formatted(props.agencyPrefix), 
        ()-> indexSvc.buildRegAgentLinkFiles(
          reader, props.outputDirPath, props.agencyPrefix)
      );
    }
    catch( Exception e ){
      throw wrapException(e, "while indexing");
    }

    log.info("finished build index process");
  }

}
