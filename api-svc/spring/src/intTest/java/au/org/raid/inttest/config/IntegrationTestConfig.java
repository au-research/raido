package au.org.raid.inttest.config;

import au.org.raid.api.spring.bean.Shared;
import au.org.raid.api.spring.config.ApiConfig;
import au.org.raid.api.util.Log;
import au.org.raid.inttest.TestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import static au.org.raid.api.util.Log.to;

@Configuration("IntegrationTestConfig")
@ComponentScan(basePackages = {
        "au.org.raid"
})
public class IntegrationTestConfig {
    public static final String REST_TEMPLATE_VALUES_ONLY_ENCODING =
            "restTemplateValuesOnlyEncoding";
//    @Bean
//    public ClientHttpRequestFactory clientHttpRequestFactory() {
//        return Shared.clientHttpRequestFactory(false);
//    }

//    @Bean
//    @Qualifier(REST_TEMPLATE_VALUES_ONLY_ENCODING)
//    public RestTemplate restTemplateWithEncodingMode(
//            ClientHttpRequestFactory factory
//    ) {
//        var restTemplate = new RestTemplate();
//    /* this is because of the silly "handles contain a slash" problem.
//    If I manually url encode the handle so that the slash is replaced, then
//    when RestTemplate sees the `%2F` (for slash) in the path, it will
//    re-urlencode the percent symbol so we end up with "%252F" in the path.
//    */
//        var defaultUriBuilderFactory = new DefaultUriBuilderFactory();
//        defaultUriBuilderFactory.setEncodingMode(
//                DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
//        restTemplate.setUriTemplateHandler(defaultUriBuilderFactory);
//        restTemplate.setRequestFactory(factory);
//        return restTemplate;
//    }

//    @Bean
//    @Primary
//    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
//        RestTemplate restTemplate = Shared.restTemplate(factory);
//        restTemplate.setRequestFactory(factory);
//        return restTemplate;
//    }

    @Bean
    public SpringMvcContract feignContract() {
        return new SpringMvcContract();
    }

//    @Bean
//    public ObjectMapper objectMapper() {
//        return new ObjectMapper().
//                // copied from ApiConfig, may not be needed but doesn't hurt
//                        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).
//                /* without this, Feign client gave error:
//                "Java 8 date/time type `java.time.LocalDate` not supported by default" */
//                        registerModule(new JavaTimeModule());
//    }

    @Bean
    public TestClient testClient(final ObjectMapper objectMapper,
                                 final SpringMvcContract contract,
                                 @Value("${raid.test.api.url}") final String apiUrl) {
        return new TestClient(objectMapper, contract, apiUrl);
    }
}