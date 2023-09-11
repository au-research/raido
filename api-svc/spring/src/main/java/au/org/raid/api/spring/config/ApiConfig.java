package au.org.raid.api.spring.config;

import au.org.raid.api.spring.config.http.converter.FormProblemDetailConverter;
import au.org.raid.api.spring.config.http.converter.XmlProblemDetailConverter;
import au.org.raid.api.util.Log;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static au.org.raid.api.util.Log.to;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
        // spring boot-up and config
        "au.org.raid.api.spring",
        // services and endpoints
        "au.org.raid.api.service",
        "au.org.raid.api.endpoint",
        "au.org.raid.api.repository",
        "au.org.raid.api.factory",
        "au.org.raid.api.validator"
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
        @PropertySource(name = "build_info",
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


}


