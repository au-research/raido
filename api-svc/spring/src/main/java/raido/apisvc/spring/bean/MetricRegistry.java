package raido.apisvc.spring.bean;

import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.search.RequiredSearch;
import io.micrometer.jmx.JmxConfig;
import io.micrometer.jmx.JmxMeterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import raido.apisvc.spring.config.environment.EnvironmentProps;
import raido.apisvc.spring.config.environment.MetricProps;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;

import static raido.apisvc.util.Log.to;

@Component
public class MetricRegistry {
  private static final Log log = to(MetricRegistry.class);
  
  public static final String HIKARI_ACQUIRE_TIMER_NAME = 
    "hikaricp.connections.acquire";
  public static final String HIKARI_CREATE_TIMER_NAME = 
    "hikaricp.connections.creation";
  public static final String HIKARI_PENDING_GAUGE_NAME = 
    "hikaricp.connections.pending";

  private EnvironmentProps env;
  private MetricProps metricProps;
  
  public CompositeMeterRegistry registry;

  public MetricRegistry(EnvironmentProps env, MetricProps metricProps) {
    this.env = env;
    this.metricProps = metricProps;
  }
  
  @PostConstruct
  public void postConstruct(){

    registry = new CompositeMeterRegistry();
    Metrics.addRegistry(registry);
    
    if( metricProps.jmxEnabled ){
      // It won't work without a bunch of network setup anyway (uses RMI 🤮)
      Guard.isTrue("Cannot use JMX in a PROD env", !env.isProd);
      log.warn("JMX registry enabled");
      // publish metrics via JMX - pretty much useless in a real environment
      registry.add(new JmxMeterRegistry(JmxConfig.DEFAULT, Clock.SYSTEM));
    }
    else {
      log.info("JMX registry not enabled");
    }
    
  }

  public void registerDataSource(HikariDataSource dataSource) {
    if( metricProps.connectionPoolMetricsEnabled ){
      log.info("registering DataSource metrics");
      dataSource.setMetricRegistry(registry);
    }
    else {
      log.info("NOT registering DataSource metrics");
    }
  }


  public void logMetricNames(){
    if( registry.getMeters().isEmpty() ){
      log.info("Mo metrics are registered");
    }
    registry.getMeters().forEach(i->
      log.with("id", i.getId().toString()).info("Registered metric"));
  }

  public void logConnectionPoolMetrics(){
    if( !metricProps.connectionPoolMetricsEnabled ){
      return;
    }

    if( !log.isInfoEnabled() ){
      return;
    }

    logTimer(HIKARI_ACQUIRE_TIMER_NAME);
    logTimer(HIKARI_CREATE_TIMER_NAME);
    logGauge(HIKARI_PENDING_GAUGE_NAME);
  }

  private void logTimer(String name) {
    RequiredSearch acquire = registry.get(name);
    log.with("stats", acquire.timer().takeSnapshot().toString()).
      info(name);
  }

  private void logGauge(String name) {
    RequiredSearch acquire = registry.get(name);
    log.with("value", acquire.gauge().value()).
      info(name);
  }

}
