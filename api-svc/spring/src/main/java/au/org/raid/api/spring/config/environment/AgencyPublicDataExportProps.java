package au.org.raid.api.spring.config.environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AgencyPublicDataExportProps {
  /* default to ./s3/... so it gets ignored by the .gitignore file and doesn't 
  pollute the root dir of the repo. */
  @Value("${AgencyPublicDataExport.allRaidsFilename:" +
    "./s3/raido-public-data-export/public-export-all-raids.ndjson}")
  public String allRaidsFilename;

  @Value("${AgencyPublicDataExport.fetchSize:500}")
  public int fetchSize;

  @Value("${AgencyPublicDataExport.logPeriodSeconds:10}")
  public int logPeriodSeconds;
}
