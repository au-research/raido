package raido.apisvc.spring.config.environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/* default values for indexing raido, override for other reg-agents */
@Component
public class BuildSearchIndexProps {
  @Value("${BuildSearchIndex.agencyPublicExportPath:" +
    "./s3/raido-public-data-export/public-export-all-raids.ndjson}")
  public String agencyPublicExportPath;

  @Value("${BuildSearchIndex.agencyPrefix:raido}")
  public String agencyPrefix;

  @Value("${BuildSearchIndex.outputDirPath:./s3/raid-search-index/raido}")
  public String outputDirPath;

  @Value("${BuildSearchIndex.logPeriodSeconds:10}")
  public int logPeriodSeconds;

  /**
   10K default value is completely arbitrary, results in link files about
   1.3MB in size
   */
  @Value("${BuildSearchIndex.maxRaidsPerFile:10000}")
  public long maxRaidsPerFile;

  @Value("${BuildSearchIndex.linkFileFormat:%s-raid-link-index-%06d.html}")
  public String linkFileFormat;
}
