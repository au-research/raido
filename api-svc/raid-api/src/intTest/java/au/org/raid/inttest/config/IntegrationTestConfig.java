package au.org.raid.inttest.config;

import au.org.raid.inttest.service.TestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import static au.org.raid.api.util.Log.to;

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
                                 @Value("${raid.test.api.url}") final String apiUrl) {
        return new TestClient(objectMapper, contract, apiUrl);
    }
}