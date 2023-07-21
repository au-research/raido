package raido.cmdline.spring.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.GenericApplicationContext;
import raido.apisvc.spring.config.ApiConfig;
import raido.apisvc.spring.config.RaidWebSecurityConfig;
import raido.apisvc.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;
import static raido.apisvc.util.ExceptionUtil.wrapIoException;
import static raido.apisvc.util.Log.to;

/**
 Plays the same role as ApiConfig, but for a non-web context (maybe 
 "NonWebConfig" would be a better name?).
 
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

  public static GenericApplicationContext configureSpring(){
    return new AnnotationConfigApplicationContext(
      CommandLineConfig.class );
  }

  /**
   Will create the dir to write to if it does not exist
   */
  public static BufferedWriter newWriter(String filePath) {
    Path path = Path.of(filePath);
    if( path.getParent() != null ){
      try {
        Files.createDirectories(path.getParent());
      }
      catch( IOException e ){
        throw wrapIoException(e, "could not create dir for path: %s", filePath);
      }
    }

    try {
      return Files.newBufferedWriter(path, UTF_8);
    }
    catch( IOException ex ){
      throw wrapIoException(ex, "while opening the writer");
    }
  }
  
  public static BufferedReader newReader(String filePath) {
    try {
      return Files.newBufferedReader(Path.of(filePath), UTF_8);
    }
    catch( IOException ex ){
      throw wrapIoException(ex, "while opening the reader");
    }
  }
}
