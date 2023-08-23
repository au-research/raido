package au.org.raid.api.spring.bean;

import au.org.raid.api.spring.config.environment.EnvironmentProps;
import au.org.raid.api.spring.config.environment.MetricProps;
import au.org.raid.api.util.Guard;
import au.org.raid.api.util.Log;
import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.search.RequiredSearch;
import io.micrometer.jmx.JmxConfig;
import io.micrometer.jmx.JmxMeterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClientBuilder;

import java.util.Map;

import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.StringUtil.areEqual;
import static au.org.raid.api.util.StringUtil.hasValue;
import static java.time.Duration.ofSeconds;

@Component
public class MetricRegistry {
  private static final Log log = to(MetricRegistry.class);

  private EnvironmentProps env;
  private MetricProps metricProps;

  public CompositeMeterRegistry registry;

  public MetricRegistry(EnvironmentProps env, MetricProps metricProps) {
    this.env = env;
    this.metricProps = metricProps;
  }

  @PostConstruct
  public void postConstruct() {
    registry = new CompositeMeterRegistry();
    Metrics.addRegistry(registry);

    configureJmxPublishing();
    configureCloudWatchPublishing();

    registerJvmGcMetrics();
    registerJvmMemoryMetrics();
    registerJvmThreadMetrics();
  }

  private void configureCloudWatchPublishing() {
    if( !metricProps.awsEnabled ){
      log.info("AWS CloudWatch metrics publishing not enabled");
      return;
    }

    log.warn("AWS CloudWatch metrics publishing enabled");

    Guard.isTrue("envName must be set if awsEnabled=true", 
      !areEqual(env.envName, EnvironmentProps.ENV_NAME_DEFAULT));
    CloudWatchMeterRegistry cloudWatchMeterRegistry =
      new CloudWatchMeterRegistry(
        setupCloudWatchConfig(),
        Clock.SYSTEM,
        cloudWatchAsyncClient());

    registry.add(cloudWatchMeterRegistry);
  }

  private void configureJmxPublishing() {
    if( !metricProps.jmxEnabled ){
      log.info("JMX metrics publishing not enabled");
      return;
    }

    // It won't work without a bunch of network setup anyway (uses RMI 🤮)
    Guard.isTrue("Cannot use JMX in a PROD env", !env.isProd);
    log.warn("JMX metrics publishing enabled");
    // publish metrics via JMX - pretty much useless in a real environment
    registry.add(new JmxMeterRegistry(JmxConfig.DEFAULT, Clock.SYSTEM));
  }

  public void registerDataSourceMetrics(HikariDataSource dataSource) {
    if( !metricProps.connectionPoolMetricsEnabled ){
      log.info("NOT registering DataSource metrics");
      return;
    }

    dataSource.setMetricRegistry(registry);
  }

  public void registerJvmGcMetrics() {
    if( metricProps.jvmGcMetricsEnabled ){
      // example code from doco doesn't use a try statement or anything
      //noinspection resource
      new JvmGcMetrics().bindTo(registry);
    }
  }

  public void registerJvmMemoryMetrics() {
    if( metricProps.jvmMemoryMetricsEnabled ){
      new JvmMemoryMetrics().bindTo(registry);
    }
  }

  public void registerJvmThreadMetrics() {
    if( metricProps.jvmThreadMetricsEnabled ){
      new JvmThreadMetrics().bindTo(registry);
    }
  }

  public void logMetricNames() {
    log.with("size", registry.getMeters().size()).
      info("Metrics registered");
    registry.getMeters().forEach(i->
      log.with("id", i.getId().toString()).debug("Registered metric"));
  }

  public void logConnectionPoolMetrics() {
    if( !metricProps.connectionPoolMetricsEnabled ){
      return;
    }

    if( !log.isInfoEnabled() ){
      return;
    }

    logTimer(LogMetric.HIKARI_ACQUIRE_TIMER_NAME);
    logTimer(LogMetric.HIKARI_CREATE_TIMER_NAME);
    logGauge(LogMetric.HIKARI_PENDING_GAUGE_NAME);
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

  private CloudWatchConfig setupCloudWatchConfig() {
    String step = ofSeconds(metricProps.awsStepSeconds).toString();
    log.with("awsStepSeconds", step).info("pushing AWS metrics to cloudwatch");

    CloudWatchConfig cloudWatchConfig = new CloudWatchConfig() {
      private Map<String, String> configuration = Map.of(
        "cloudwatch.namespace", "api-svc-" + env.envName,
        "cloudwatch.step", step);

      @Override
      public String get(String key) {
        return configuration.get(key);
      }
    };
    return cloudWatchConfig;
  }

  public CloudWatchAsyncClient cloudWatchAsyncClient() {
    CloudWatchAsyncClientBuilder builder = CloudWatchAsyncClient.builder();

    // even when running inside AWS it can't figure this out?
    Guard.hasValue("awsRegion must be set if awsEnabled=true", 
      metricProps.awsRegion);
    Region region = Region.of(metricProps.awsRegion);
    builder = builder.region(region);

    if( hasValue(metricProps.awsProfile) ){
      log.with("awsProfile", metricProps.awsProfile).
        info("Metrics will be published to AWS via profile credentials");
      Guard.hasValue(
        "must set region if awsProfile is set",
        metricProps.awsRegion);
      builder = builder.
        credentialsProvider(
          ProfileCredentialsProvider.create(metricProps.awsProfile));
    }
    else {
      log.info("Metrics will be published to AWS via machine credentials");
    }

    return builder.build();
  }

}
