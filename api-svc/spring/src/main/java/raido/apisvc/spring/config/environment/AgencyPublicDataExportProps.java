package raido.apisvc.spring.config.environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AgencyPublicDataExportProps {
  @Value("${AgencyPublicDataExport.allRaidsFilename:all-raids.ndjson}")
  public String allRaidsFilename;

  @Value("${AgencyPublicDataExport.fetchSize:500}")
  public int fetchSize;

  @Value("${AgencyPublicDataExport.logPeriodSeconds:10}")
  public int logPeriodSeconds;
}
