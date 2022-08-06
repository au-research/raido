package raido.inttest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.DefaultUriBuilderFactory.EncodingMode;
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
    var restTemplate = new RestTemplate();

    /* this is because of the stupid "handles contain a slash" problem.
    If I manually url encode the handle so that the slash is replaced, then
    when RestTemplate sees the `%2F` (for slash) in the path, it will 
    re-urlencode the percent symbol so we we end up with "%252F" in the path.
    Probably going to have write some kind of custom handle-aware UriBuilder,
    but this'll do for the moment. */
    var defaultUriBuilderFactory = new DefaultUriBuilderFactory();
    defaultUriBuilderFactory.setEncodingMode(EncodingMode.VALUES_ONLY);
    restTemplate.setUriTemplateHandler(defaultUriBuilderFactory);

    return restTemplate;
  }

}
