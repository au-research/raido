package raido.inttest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.client.RestTemplate;
import raido.apisvc.spring.config.ApiConfig;
import raido.apisvc.util.Log;

import static raido.apisvc.util.Log.to;

@Configuration
@ComponentScan(basePackages = {
  // test config
  "raido.inttest.config", "raido.inttest.spring",
  
  // services used by tests and infra
  "raido.inttest.service", 
  
  // leverage prod configuration for inttest
  "raido.apisvc.spring.config.database", 
  "raido.apisvc.spring.config.environment"
})
/** this allows us to pick up the same props as used by the prod code 
 (for DB, jwtSecret, etc.) 
 */
@PropertySource(name = "user_config_environment",
  value = ApiConfig.ENV_PROPERTIES, ignoreResourceNotFound = true)
@PropertySource(name = "inttest_working_dir_environment",
  value = "./inttest.properties",
  ignoreResourceNotFound = true)
@PropertySource(name = "inttest_user_config_environment",
  value = "file:///${user.home}/.config/raido-v2/api-svc-inttest.properties",
  ignoreResourceNotFound = true)
public class IntegrationTestConfig {
  private static final Log log = to(IntegrationTestConfig.class);
  
  /** Without this, @Value annotation don't resolve ${} placeholders */
  @Bean
  public static PropertySourcesPlaceholderConfigurer
  propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  /** for talking to the api-svc over the wire */
  @Bean
  public RestTemplate restTemplate(){
    return new RestTemplate();
  }

}
