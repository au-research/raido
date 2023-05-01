package raido.apisvc.spring.config.environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MetricProps {
  /**
   Enables metrics do do with the Hikari JDBC connection pool.
   */
  @Value("${MetricRegistry.connectionPoolMetricsEnabled:true}")
  public boolean connectionPoolMetricsEnabled;

  @Value("${MetricRegistry.jvmGcMetricsEnabled:true}")
  public boolean jvmGcMetricsEnabled;

  @Value("${MetricRegistry.jvmMemoryMetricsEnabled:true}")
  public boolean jvmMemoryMetricsEnabled;

  @Value("${MetricRegistry.jvmThreadMetricsEnabled:true}")
  public boolean jvmThreadMetricsEnabled;

  /**
   Enables publishing metrics via JMX (intended for local dev only).
   See metrics-jmx.md
   */
  @Value("${MetricRegistry.jmxEnabled:false}")
  public boolean jmxEnabled;

  /**
   Whether to push metrics directly to AWS cloudwatch from api-svc.
   Expected to be false for most local-dev, CI environments and enabled in 
   higher level environments like DEMO and PROD.
   */
  @Value("${MetricRegistry.awsEnabled:false}")
  public boolean awsEnabled;

  /**
   The ~/.aws/config profile to use for AWS credentials.
   Only needs to be set if you're trying to enable AWS from a local dev env.
   */
  @Value("${MetricRegistry.awsProfile:}")
  public String awsProfile;

  /** used when using awsProfile, because the AWS client won't even attempt 
   to figure out a default region when running outside of AWS.  
   */
  @Value("${MetricRegistry.awsRegion:ap-southeast-2}")
  public String awsRegion;
  
  @Value("${MetricRegistry.awsStepSeconds:60}")
  public int awsStepSeconds;
  
}
