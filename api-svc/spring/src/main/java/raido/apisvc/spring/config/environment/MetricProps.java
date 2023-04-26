package raido.apisvc.spring.config.environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import raido.apisvc.util.Log;

import static raido.apisvc.util.Log.to;

@Component
public class MetricProps {
  /**
   Enables metrics do do with the Hikari JDBC connection pool.
   */
  @Value("${MetricRegistry.connectionPoolMetricsEnabled:true}")
  public boolean connectionPoolMetricsEnabled;

  /**
   Enables publishing metrics via JMX (intended for local dev only).
   See jmx-metrics.md
   */
  @Value("${MetricRegistry.jmxEnabled:true}")
  public boolean jmxEnabled;
}
