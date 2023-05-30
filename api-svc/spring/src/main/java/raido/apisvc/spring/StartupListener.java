package raido.apisvc.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.stereotype.Component;
import raido.apisvc.spring.bean.MetricRegistry;
import raido.apisvc.spring.config.environment.DataSourceProps;
import raido.apisvc.spring.config.environment.EnvironmentProps;
import raido.apisvc.util.JvmUtil;
import raido.apisvc.util.Log;

import java.time.LocalDateTime;

import static java.lang.Runtime.getRuntime;
import static raido.apisvc.util.Log.to;

@Component
public class StartupListener implements
  ApplicationListener<ContextRefreshedEvent> 
{
  
  private static final Log log = to(StartupListener.class);

  @Value("${raido.greeting:no greeting config}")
  private String greeting;
 
  private LocalDateTime startTime;

  private DataSourceProps dsProps;
  private EnvironmentProps envProps;
  private MetricRegistry metricReg;

  public StartupListener(
    DataSourceProps dsProps, 
    EnvironmentProps envProps,
    MetricRegistry metricReg
  ) {
    this.dsProps = dsProps;
    this.envProps = envProps;
    this.metricReg = metricReg;
  }

  @Override public void onApplicationEvent(ContextRefreshedEvent event) {
    log.with("eventSource", event.getSource()).
      info("Greeting - %s", greeting );
    log.with("envName", envProps.envName).
      with("nodeId", envProps.nodeId).
      with("isProd", envProps.isProd).
      info("Environment");
    log.with("url", dsProps.getUrl()).
      with("username", dsProps.getUsername()).
      info("DataSource");
    JvmUtil.logSysProps();
    JvmUtil.logBuildInfo();
    JvmUtil.logMemoryInfo("startup");
    log.with("availableProcessors", getRuntime().availableProcessors()).
      info("JVM info");
    metricReg.logMetricNames();

    this.startTime = LocalDateTime.now();

    if( event.getApplicationContext().getEnvironment() 
      instanceof ConfigurableEnvironment cfgEnv
    ){
      logPropertySources(
        event.getApplicationContext().getDisplayName(), cfgEnv );
    }
    else {
      log.with("environment",
          event.getApplicationContext().getEnvironment().getClass().getName() ).
        info("non-configurable environment");
    }

  }

  /**
   Actually "ContextRefreshed" not "Start" time, but close enough for now.
   */
  public LocalDateTime getStartTime() {
    return startTime;
  }

  public static void logPropertySources(
    String displayName, ConfigurableEnvironment env
  ){
    MutablePropertySources propertySources = env.getPropertySources();
    
    log.with("displayName", displayName).
      with("count", propertySources.size()).
      info("AppContext propertySources");
    
    for( PropertySource<?> i : propertySources){
      String propertySourceName = i.getName().toLowerCase();

      if( i instanceof ResourcePropertySource iResource ){
        log.with("name", iResource.getName()).
          with("resourceName", iResource.withResourceName()).
          info("resource source");
      }
      else {
        log.with("name", propertySourceName).
          info("non-resource source");
      }

    }
  }
}