package raido.cmdline;

import raido.apisvc.service.export.BuildSearchIndexService;
import raido.apisvc.spring.config.environment.BuildSearchIndexProps;
import raido.apisvc.util.DateUtil;
import raido.apisvc.util.Log;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static raido.apisvc.util.ExceptionUtil.wrapException;
import static raido.apisvc.util.IdeUtil.formatClickable;
import static raido.apisvc.util.JvmUtil.normaliseJvmDefaults;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.infoLogExecutionTime;
import static raido.cmdline.spring.config.CommandLineConfig.configureSpring;
import static raido.apisvc.util.IoUtil.newReader;
import static raido.apisvc.util.IoUtil.newWriter;
import static raido.cmdline.util.SiteMapUtil.writeSitemapHeader;
import static raido.cmdline.util.SiteMapUtil.writeSitemapTrailer;
import static raido.cmdline.util.SiteMapUtil.writeSitemapLocation;

/**
 This is intended to be run for each reg-agency, path values are defaulted for
 raido but those will be overridden for other reg-agencies.
 
 Look at /doc/search-index/sitemap-search-index.md for an overview of the 
 sitemap structure.
 
 The input file is intended to be a public-data-export written by 
 {@link AgencyPublicDataExport} (or similar code that the reg-agent is free
 to implement themselves).
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

    List<String> indexLinkFilePaths;
    try(
      var reader = newReader(publicExportPath);
    ) {
      indexLinkFilePaths = infoLogExecutionTime(log, 
        "building index for %s".formatted(props.agencyPrefix), 
        ()-> indexSvc.buildRegAgentLinkFiles(
          reader, props.outputDirPath, props.agencyPrefix)
      );
    }
    catch( Exception e ){
      throw wrapException(e, "while indexing");
    }

    writeSitemapFile(props, indexLinkFilePaths);
    
    log.info("finished build index process");
  }

  public static void writeSitemapFile(
    BuildSearchIndexProps props,
    List<String> indexLinkPaths
  ){
    String sitemapFilePath = "%s/%s-sitemap.xml".formatted(
      props.outputDirPath, props.agencyPrefix);
    String today = DateUtil.formatIsoDate(LocalDate.now());

    try( var writer = newWriter(sitemapFilePath) ) {
      writeSitemapHeader(writer);
      indexLinkPaths.forEach(iLinkFilePath->{
        String iFileName = Path.of(iLinkFilePath).getFileName().toString();
        writeSitemapLocation(
          writer,
          "https://raid.org/%s/%s".formatted(props.agencyPrefix, iFileName),
          today
        );
      });
      writeSitemapTrailer(writer);
    }
    catch( Exception e ){
      throw wrapException(e, "while writing sitemap: %s", sitemapFilePath);
    }
  }
  
}

