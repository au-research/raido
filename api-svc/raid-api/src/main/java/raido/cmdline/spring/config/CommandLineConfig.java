package raido.cmdline.spring.config;

import au.org.raid.api.spring.config.ApiConfig;
import au.org.raid.api.spring.config.RaidWebSecurityConfig;
import au.org.raid.api.util.Log;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;

import static au.org.raid.api.util.Log.to;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

/**
 * Plays the same role as ApiConfig, but for a non-web context (maybe
 * "NonWebConfig" would be a better name?).
 */
@Configuration("CommandLineConfig")
@ComponentScan(
        basePackages = {
                // spring boot-up and config
                "raido.cmdline.spring",
                "raido.apisvc.spring",

                // services etc
                "raido.apisvc.repository",
                "raido.apisvc.factory",
                "raido.apisvc.service",
        },
        excludeFilters = {
                @ComponentScan.Filter(type = ASSIGNABLE_TYPE, classes = {
                        ApiConfig.class, RaidWebSecurityConfig.class
                }),
        }
)
@PropertySources({
        @PropertySource(name = "working_dir_environment",
                value = "file:./env.properties",
                ignoreResourceNotFound = true),

        @PropertySource(name = "hardcode_environment",
                value = "classpath:./env.properties",
                ignoreResourceNotFound = true),

        // It might make sense to use different property file names?
        @PropertySource(name = "user_config_environment2",
                value = ApiConfig.ENV_PROPERTIES2, ignoreResourceNotFound = true),

        @PropertySource(name = "user_config_secret2",
                value = ApiConfig.SECRET_PROPERTIES2, ignoreResourceNotFound = true),
})
public class CommandLineConfig {
    private static final Log log = to(CommandLineConfig.class);

    public static GenericApplicationContext configureSpring() {
        return new AnnotationConfigApplicationContext(CommandLineConfig.class);
    }
}