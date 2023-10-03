package au.org.raid.api.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
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
public class ApiConfig{
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
}


