package au.org.raid.api;

import au.org.raid.api.jetty.EmbeddedJetty;
import au.org.raid.api.spring.config.ApiConfig;
import au.org.raid.api.util.JvmUtil;
import au.org.raid.api.util.Log;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class Api {
  public static final int PORT = 8080;
  
  private static final Log log = Log.to(Api.class);

  static {
    /* https://stackoverflow.com/a/43242620/924597 
    Originally introduced for getting the pgjdbc driver logging working. */
    SLF4JBridgeHandler.install();
  }
  
  public static void main(String... args) throws Exception {
    JvmUtil.normaliseJvmDefaults();
    
    var jetty = new EmbeddedJetty();
    /* IDEA whining about not using try-with-resources, but pretty sure we  
    don't want that given we let the main method return and rely on the 
    daemon threads to keep the JVM alive.  */
    //noinspection resource
    jetty.configureHttpConnector(PORT);
    jetty.addServletContainerInitializer( "Api.main", (sci, ctx) -> 
        EmbeddedJetty.initApplicationContext(ctx, ApiConfig.class) );

    /* Will be called when pressing ctrl-c, or `docker stop` is issued.
    Note that when Jetty is shutdown cleanly like this it will call 
    `ConfigurationApplicationContext.close()` which will shutdown Spring 
    cleanly - with contextDestroyed() etc. being called. */
    Runtime.getRuntime().addShutdownHook(
      new Thread(jetty::shutdown, "app-shutdown") );
    
    log.info("starting the Raido Jetty server");
    jetty.startJoin();
  }

}