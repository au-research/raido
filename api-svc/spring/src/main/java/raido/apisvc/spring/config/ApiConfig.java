package raido.apisvc.spring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import raido.apisvc.service.apids.ApidsService;
import raido.apisvc.service.doi.DoiService;
import raido.apisvc.service.orcid.OrcidService;
import raido.apisvc.service.ror.RorService;
import raido.apisvc.service.stub.apids.InMemoryApidsServiceStub;
import raido.apisvc.service.stub.doi.InMemoryDoiServiceStub;
import raido.apisvc.service.stub.orcid.InMemoryOrcidServiceStub;
import raido.apisvc.service.stub.ror.InMemoryRorServiceStub;
import raido.apisvc.spring.config.environment.ApidsProps;
import raido.apisvc.spring.config.environment.EnvironmentProps;
import raido.apisvc.spring.config.environment.InMemoryStubProps;
import raido.apisvc.spring.config.http.converter.FormProblemDetailConverter;
import raido.apisvc.spring.config.http.converter.XmlProblemDetailConverter;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static raido.apisvc.util.Log.to;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
  // spring boot-up and config
  "raido.apisvc.spring", 
  // services and endpoints
  "raido.apisvc.service",
  "raido.apisvc.endpoint",
  "raido.apisvc.repository",
  "raido.apisvc.factory"
})
@PropertySources({
  /* This is NOT for you to put an `env.properties` file with credentials in the 
  source tree!
  This is a convenience to allow simple deployments to just dump the 
  uberJar and config in a directory and run the Java command from that directly.
  It's possible to maintain multiple different configurations on the same 
  machine by putting config in separate directories and executing from those 
  directories. We use Docker to encapsulate in a real setup, but this can be
  useful sometimes to run multiple configurations on the same machine. 
  */
  @PropertySource(name = "working_dir_environment",
    value = "file:./env.properties",
    ignoreResourceNotFound = true),
  
  /* This is NOT for you to put an `env.properties` file with credentials in the 
  source tree!
  This exists so we can override defaults values for intTests, on the rare 
  occasion that makes sense (usually temporarily).
  Actual default values for configuration should be embedding in the 
  configuration annotation, see the implementations of the beans in 
  /raido/apisvc/spring/config/environment */
  @PropertySource(name = "hardcode_environment",
    value = "classpath:./env.properties",
    ignoreResourceNotFound = true),
  
  /* This is where you should put credentials for standard development workflow,
  far away from the source tree, in a standard location that usually has better
  OS-level protections (permissions, etc.)
  During standard development cycle, uses hardcoded default XDG location for 
  config files. IMPROVE: use XDG_CONFIG_HOME env variable */
  @PropertySource(name = "user_config_environment",
    value = ApiConfig.ENV_PROPERTIES, ignoreResourceNotFound = true),
  @PropertySource(name = "user_config_environment2",
    value = ApiConfig.ENV_PROPERTIES2, ignoreResourceNotFound = true),
  
  /* we put secrets into a separate file so we can keep env stuff in a nice
  visible SSM String param, and secrets can be in a SecureString or even 
  in a proper secret in AWS SecretManager. Also, so we can easily log the
  env properties without risking logging secret properties. */
  @PropertySource(name = "user_config_secret",
    value = ApiConfig.SECRET_PROPERTIES, ignoreResourceNotFound = true),
  @PropertySource(name = "user_config_secret2",
    value = ApiConfig.SECRET_PROPERTIES2, ignoreResourceNotFound = true),
  
  // added to jar at build time by gradle springBoot.buildInfo config
  @PropertySource( name = "build_info",
    value = "classpath:META-INF/build-info.properties",
    ignoreResourceNotFound = true)
})
public class ApiConfig implements WebMvcConfigurer {
  /* IMPROVE:STO after everything is stabilised on the new `raido` repo name,
  delete the old raido-v2 references. */
  public static final String ENV_PROPERTIES = "file:///${user.home}/" +
    ".config/raido-v2/api-svc-env.properties";
  public static final String SECRET_PROPERTIES = "file:///${user.home}/" +
    ".config/raido-v2/api-svc-secret.properties";
  public static final String ENV_PROPERTIES2 = "file:///${user.home}/" +
    ".config/raido/api-svc-env.properties";
  public static final String SECRET_PROPERTIES2 = "file:///${user.home}/" +
    ".config/raido/api-svc-secret.properties";

  private static final Log log = to(ApiConfig.class);
  
  


  /**
   This replaces the default resolver, I was having trouble with ordering and
   besides - no reason to have the default if it shouldn't be invoked.
   */
//  @Bean
//  public HandlerExceptionResolver handlerExceptionResolver(
//    @Value("${redactErrorDetails:true}") boolean redactErrorDetails
//  ) {
//    return new RedactingExceptionResolver(redactErrorDetails);
//  }

  /** Without this, @Value annotation don't resolve ${} placeholders */
  @Bean
  public static PropertySourcesPlaceholderConfigurer
  propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  /*
   Given that we added WRITE_DATES_AS_TIMESTAMPS for this (which I think was 
   for the REST API endpoints - I don't understand why we haven't needed to 
   register JavaTimeModule for this? Like the raidMetadata mapper and the 
   mapper used by the feign client for integration tests - we had to register
   the module for those usages, why not here?
  */
  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper().
      /* from memory, this was to get the Spring REST API endpoints writing 
      datetime the way I wanted. */
      disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).
      registerModule(new JavaTimeModule());
  }
  
  @Bean
  @Primary
  public static RestTemplate restTemplate(ClientHttpRequestFactory factory){
    MappingJackson2XmlHttpMessageConverter xmlConverter =
      new MappingJackson2XmlHttpMessageConverter();
    xmlConverter.setSupportedMediaTypes(
      singletonList(MediaType.APPLICATION_XML) );

    MappingJackson2HttpMessageConverter jsonConverter =
      new MappingJackson2HttpMessageConverter();

    List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
    messageConverters.add(xmlConverter);
    messageConverters.add(jsonConverter);
    messageConverters.add(new FormHttpMessageConverter());

    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setMessageConverters(messageConverters);
    restTemplate.setRequestFactory(factory);

    return restTemplate;
  }

  @Bean
  public static RestTemplate nonRedirectingRestTemplate(){
    MappingJackson2XmlHttpMessageConverter xmlConverter =
      new MappingJackson2XmlHttpMessageConverter();
    xmlConverter.setSupportedMediaTypes(
      singletonList(MediaType.APPLICATION_XML) );

    MappingJackson2HttpMessageConverter jsonConverter =
      new MappingJackson2HttpMessageConverter();

    List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
    messageConverters.add(xmlConverter);
    messageConverters.add(jsonConverter);
    messageConverters.add(new FormHttpMessageConverter());

    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setMessageConverters(messageConverters);
    restTemplate.setRequestFactory(clientHttpRequestFactory(false));

    return restTemplate;
  }

  @Bean 
  public static ClientHttpRequestFactory clientHttpRequestFactory(){
    return clientHttpRequestFactory(true);
  }

  public static ClientHttpRequestFactory clientHttpRequestFactory(
    boolean followRedirects
  ) {
    OkHttpClient client = new OkHttpClient.Builder().
      followRedirects(followRedirects).
      build();

    return new OkHttp3ClientHttpRequestFactory(client) {
      @Override
      public void destroy() throws IOException {
        /* copy pasted from the spring impl, because it won't do this for a 
        provide client. */
        Cache cache = client.cache();
        if( cache != null ){
          cache.close();
        }
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
      }
    };
  }
  
  /* Not sure if we should be using "configure" or "extend".  AFAIK, this here
  is resetting the default converters, so this converter is the only one.
  Does this mean our server doesn't support other content types?
  Not sure if that's a good thing or a bad thing. 
  Whatever else this is used for, it is used by the HttpEntityMethodProcessor
  to create the "return value" from a "ResponseEntity", so if you're having 
  conversion errors caused by weird contentTypes or accept headers, this might
  be the place you need to deal with it.
  */
  @Override
  public void configureMessageConverters(
    List<HttpMessageConverter<?>> converters
  ) {
    MappingJackson2HttpMessageConverter jsonConverter =
      new MappingJackson2HttpMessageConverter();

    /* By default dates looked like `1662077155.409857400`. 
    The app-client openapi generated ts that converted this to dates with code 
    like `new Date(json['startDate']` which did not parse the date properly.
    https://stackoverflow.com/a/67078987/924597 */
    jsonConverter.getObjectMapper().
      disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    converters.add(jsonConverter);
    
    converters.add(new FormProblemDetailConverter());
    converters.add(new XmlProblemDetailConverter());

    // prototype: used for returning static HTML from an endpoint
//    converters.add(PublicExperimental.getHtmlStringConverter());
  }
  
  @Bean @Primary
  public ApidsService apidsService(
    EnvironmentProps envConfig,
    InMemoryStubProps stubProps,
    ApidsProps apidsConfig,
    RestTemplate rest
  ){
    /* IMPROVE: I'm fairly sure I'm not doing this the "spring way" */
    if( stubProps.apidsInMemoryStub ){
      Guard.isTrue("Cannot use InMemoryApidsServiceStub in a PROD env",
        !envConfig.isProd);
      log.warn("using the in-memory ORCID service");
      return new InMemoryApidsServiceStub(stubProps, envConfig);
    }

    /* now we aren't forced to set the secret if we're not using the real 
    APIDS service - unexpected benefit! */
    Guard.allHaveValue("must set ApidsProps values",
      apidsConfig.secret, apidsConfig.appId, apidsConfig.serviceUrl);
    return new ApidsService(apidsConfig, rest);
  }
    
  @Bean @Primary
  public OrcidService orcidService(
    EnvironmentProps envConfig,
    InMemoryStubProps stubProps,
    RestTemplate rest
  ){
    if( stubProps.orcidInMemoryStub ){
      Guard.isTrue("Cannot use InMemoryOrcidServiceStub in a PROD env",
        !envConfig.isProd);
      log.warn("using the in-memory ORCID service");
      return new InMemoryOrcidServiceStub(stubProps);
    }
    
    return new OrcidService(rest);
  }
  
  @Bean @Primary
  public RorService rorService(
    EnvironmentProps envConfig,
    InMemoryStubProps stubProps,
    RestTemplate rest
  ){
    if( stubProps.rorInMemoryStub ){
      Guard.isTrue("Cannot use InMemoryRorServiceStub in a PROD env",
        !envConfig.isProd);
      log.warn("using the in-memory ROR service");
      return new InMemoryRorServiceStub(stubProps);
    }
    
    return new RorService(rest);
  }


  @Bean @Primary
  public DoiService doiService(
    EnvironmentProps envConfig,
    InMemoryStubProps stubProps,
    @Qualifier("nonRedirectingRestTemplate")
    RestTemplate rest
  ){
    if( stubProps.rorInMemoryStub ){
      Guard.isTrue("Cannot use InMemoryDoiServiceStub in a PROD env",
        !envConfig.isProd);
      log.warn("using the in-memory ROR service");
      return new InMemoryDoiServiceStub(stubProps);
    }

    return new DoiService(rest);
  }
}


