package raido.inttest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan(basePackages = {
  "raido.inttest.config", "raido.inttest.spring"})
@PropertySource(name = "inttest_working_dir_environment",
  value = "./inttest.properties",
  ignoreResourceNotFound = true)
@PropertySource(name = "inttest_user_config_environment",
  value = "file:///${user.home}/.config/raido-v2/api-svc-inttest.properties",
  ignoreResourceNotFound = true)
public class IntegrationTestConfig {
  
  @Bean
  public RestTemplate restTemplate(){
    return new RestTemplate();
  }

}
