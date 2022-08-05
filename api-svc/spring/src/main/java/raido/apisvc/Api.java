package raido.apisvc;

import org.slf4j.bridge.SLF4JBridgeHandler;
import raido.apisvc.jetty.EmbeddedJetty;
import raido.apisvc.spring.config.ApiConfig;
import raido.apisvc.util.Log;

import static raido.apisvc.util.JvmUtil.normaliseJvmDefaults;
import static raido.apisvc.util.Log.to;

public class Api {
  public static final int PORT = 8080;
  
  private static final Log log = to(Api.class);

  static {
    /* https://stackoverflow.com/a/43242620/924597 
    Originally introduced for getting the pgjdbc driver stuff. */
    SLF4JBridgeHandler.install();
  }
  
  public static void main(String... args) throws Exception {
    normaliseJvmDefaults();
    
    var jetty = new EmbeddedJetty();
    /* IDEA whining about not using try-with-resources, but pretty sure we  
    don't want that given we let the main method return and rely on the 
    daemon threads to keep the JVM alive.  */
    //noinspection resource
    jetty.configureHttpConnector(PORT);
    jetty.addServletContainerInitializer( (sci, ctx) -> 
        ApiConfig.initServletContext(ctx) );

    // Will be called when pressing ctrl-c, for example.
    Runtime.getRuntime().addShutdownHook(
      new Thread(jetty::shutdown, "app-shutdown") );
    
    log.info("starting the server");
    jetty.startJoin();
  }

}