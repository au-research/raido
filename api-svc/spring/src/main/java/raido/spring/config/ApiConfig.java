package raido.spring.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import raido.util.Log;


import static java.util.Collections.emptySet;
import static raido.util.Log.to;

@Configuration
@EnableWebMvc
@EnableWebSecurity
@ComponentScan(basePackages = {
  "raido.spring.config", "raido.service", "raido.endpoint" })
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
  value = "./env.properties",
  ignoreResourceNotFound = true)
/* This is where you should put credentials for standard development workflow,
far away from the source tree, in a standard location that usually has better
OS-level protections (permissions, etc.)
During standard development cycle, uses hardcoded default XDG location for 
config files. IMPROVE: use XDG_CONFIG_HOME env variable */
@PropertySource(name = "user_config_environment",
  value = "file:///${user.home}/.config/raido-v2/api-svc-env.properties",
  ignoreResourceNotFound = true)
public class ApiConfig {
  private static final Log log = to(ApiConfig.class);

  public static AnnotationConfigWebApplicationContext initServletContext(
    ServletContext ctx
  ) {
    log.with("contextName", ctx.getServletContextName()).
      with("contextPath", ctx.getContextPath()).
      info("initServletContext()");
    // Create the 'root' Spring application context
    AnnotationConfigWebApplicationContext rootContext =
      new AnnotationConfigWebApplicationContext();
    rootContext.register(ApiConfig.class);
    // Manage the lifecycle of the root application context
    ctx.addListener(new ContextLoaderListener(rootContext));

    // probs not necessary if Spring http config is set to STATELESS 
    ctx.setSessionTrackingModes(emptySet());

    /* Register and map the dispatcher servlet
     Example code used a separate Spring context of the servlet, but I don't
     see why that's necessary. */
    DispatcherServlet servlet = new DispatcherServlet(rootContext);
    // Make sure NoHandlerFound is handled by custom HandlerEexceptionResolver
    servlet.setThrowExceptionIfNoHandlerFound(true);
    ServletRegistration.Dynamic dispatcher = 
      ctx.addServlet("raido_dispatcher", servlet);
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping("/");

    /* Dunno why, but Spring doesn't find WebApplicationInitializer 
    interfaces automatically, so we have to call onStartup() directly. */
    new AbstractSecurityWebApplicationInitializer(){}.onStartup(ctx);
    
    return rootContext;
  }

  /**
   This replaces the default resolver, I was having trouble with ordering and
   besides - no reason to have the default if it shouldn't be invoked.
   */
  @Bean
  public HandlerExceptionResolver handlerExceptionResolver(
    @Value("${redactErrorDetails:true}") boolean redactErrorDetails
  ) {
    return new RedactingExceptionResolver(redactErrorDetails);
  }

}


