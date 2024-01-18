package au.org.raid.inttest.config;

import au.org.raid.inttest.service.TestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "au.org.raid"
})
public class IntegrationTestConfig {
    @Bean
    public SpringMvcContract feignContract() {
        return new SpringMvcContract();
    }

    @Bean
    public TestClient testClient(final ObjectMapper objectMapper,
                                 final SpringMvcContract contract,
                                 @Value("${raid.test.api.url}") final String apiUrl
    ) {
        return new TestClient(objectMapper, contract, apiUrl);
    }

    @Bean
    public ObjectMapper objectMapper() {
        final var objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        return objectMapper;
    }
}