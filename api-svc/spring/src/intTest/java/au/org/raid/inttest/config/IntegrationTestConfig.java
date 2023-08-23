package au.org.raid.inttest.config;

import au.org.raid.api.spring.StartupListener;
import au.org.raid.api.spring.bean.MetricRegistry;
import au.org.raid.api.spring.bean.Shared;
import au.org.raid.api.spring.config.ApiConfig;
import au.org.raid.api.spring.config.environment.DataSourceProps;
import au.org.raid.api.spring.config.environment.EnvironmentProps;
import au.org.raid.api.util.Log;
import au.org.raid.inttest.TestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import static au.org.raid.api.util.Log.to;

@Configuration("IntegrationTestConfig")
@ComponentScan(basePackages = {
        // test config
        "au.org.raid.inttest.config",
        "au.org.raid.inttest.auth",

        // services used by tests and infra
//  "raido.inttest.service",
//  "raido.inttest.service",

        // leverage prod configuration for inttest
        "au.org.raid.api.spring.config.database",
        "au.org.raid.api.spring.config.environment",

  /* DB config depends on MetricRegistry, so I just threw this package in there 
  might want to factor things out if bean package gets too messy.
  By default, metrics are enabled for api-svc context, disabled for int-test.
  I wonder if there's a way to make use of metrics from int-tests somehow? 
  */
        "au.org.raid.api.spring.bean"
})
/** reading from api-svc-env.props allows us to pick up the same props as used
 by the actual api-server, so we don't have to configure stuff like env specific 
 DB properties twice for the int-tests and the prod api-svc code. */
@PropertySource(name = "user_config_environment2",
        value = ApiConfig.ENV_PROPERTIES2, ignoreResourceNotFound = true)
/** pick up any "default" config for int tests that we might want to set 
 in the source tree  - this file is intended to be picked up from
 `.../src/intTest/java` */
@PropertySource(name = "inttest_working_dir_environment",
        value = "./inttest.properties",
        ignoreResourceNotFound = true)
/** Allow env-specific configuration of the int-tests, separate from the 
 config of the prod ApeConfig context.
 For example, the legacy "liveToken" for integration tests, which we cannot
 commit to the in-tree `inttest.properties` file. */
@PropertySource(name = "inttest_user_config_environment",
        value = "file:///${user.home}/.config/raido/api-svc-inttest.properties",
        ignoreResourceNotFound = true)
public class IntegrationTestConfig {
    public static final String REST_TEMPLATE_VALUES_ONLY_ENCODING =
            "restTemplateValuesOnlyEncoding";
    private static final Log log = to(IntegrationTestConfig.class);

    /**
     * Without this, @Value annotation don't resolve ${} placeholders
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer
    propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public static ClientHttpRequestFactory clientHttpRequestFactory() {
        return Shared.clientHttpRequestFactory(false);
    }

    @Bean
    @Qualifier(REST_TEMPLATE_VALUES_ONLY_ENCODING)
    public static RestTemplate restTemplateWithEncodingMode(
            ClientHttpRequestFactory factory
    ) {
        var restTemplate = new RestTemplate();
    /* this is because of the silly "handles contain a slash" problem.
    If I manually url encode the handle so that the slash is replaced, then
    when RestTemplate sees the `%2F` (for slash) in the path, it will
    re-urlencode the percent symbol so we end up with "%252F" in the path.
    */
        var defaultUriBuilderFactory = new DefaultUriBuilderFactory();
        defaultUriBuilderFactory.setEncodingMode(
                DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
        restTemplate.setUriTemplateHandler(defaultUriBuilderFactory);
        restTemplate.setRequestFactory(factory);
        return restTemplate;
    }

    @Bean
    @Primary
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        RestTemplate restTemplate = Shared.restTemplate(factory);
        restTemplate.setRequestFactory(factory);
        return restTemplate;
    }

    @Bean
    public SpringMvcContract feignContract() {
        return new SpringMvcContract();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().
                // copied from ApiConfig, may not be needed but doesn't hurt
                        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).
                /* without this, Feign client gave error:
                "Java 8 date/time type `java.time.LocalDate` not supported by default" */
                        registerModule(new JavaTimeModule());
    }

    /* Defining this bean demonstrates to the programmers (via the log output)
    that there are *two* Spring ApplicationContext objects that are used when
    an intTest is running, and that they have some separate and some shared
    config. */
    @Bean
    public StartupListener startupListener(
            DataSourceProps dsProps,
            EnvironmentProps envProps,
            MetricRegistry metricReg
    ) {
        return new StartupListener(dsProps, envProps, metricReg);
    }


    @Bean
    public TestClient testClient(final ObjectMapper objectMapper, final SpringMvcContract contract, final IntTestProps props) {
        return new TestClient(objectMapper, contract, props);
    }
}