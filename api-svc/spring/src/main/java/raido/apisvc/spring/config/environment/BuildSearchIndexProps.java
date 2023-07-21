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

  @Value("${BuildSearchIndex.outputDirPath:./s3/raid-search-index}")
  public String outputDirPath;

  @Value("${BuildSearchIndex.logPeriodSeconds:10}")
  public int logPeriodSeconds;
}
