package raido.cmdline;

import au.org.raid.api.service.export.BuildSearchIndexService;
import au.org.raid.api.spring.config.environment.BuildSearchIndexProps;
import au.org.raid.api.util.DateUtil;
import au.org.raid.api.util.Log;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static au.org.raid.api.util.ExceptionUtil.wrapException;
import static au.org.raid.api.util.IdeUtil.formatClickable;
import static au.org.raid.api.util.IoUtil.newReader;
import static au.org.raid.api.util.IoUtil.newWriter;
import static au.org.raid.api.util.JvmUtil.normaliseJvmDefaults;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.ObjectUtil.infoLogExecutionTime;
import static raido.cmdline.spring.config.CommandLineConfig.configureSpring;
import static raido.cmdline.util.SiteMapUtil.*;

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

