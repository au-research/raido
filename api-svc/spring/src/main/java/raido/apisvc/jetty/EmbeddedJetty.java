package raido.apisvc.jetty;

import jakarta.servlet.ServletContainerInitializer;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import raido.apisvc.util.Log;

public class EmbeddedJetty {

  private final Log log = Log.to(getClass());
  private final Server server;

  public EmbeddedJetty() {
    QueuedThreadPool qtp = new QueuedThreadPool();
    // by default its "qtp${hashcode}", I like this better
    qtp.setName("Jetty");
    server = new Server(qtp);
  }

  public void addServletContainerInitializer(ServletContainerInitializer sci) {
    ServletContextHandler contextHandler = new ServletContextHandler();
    server.setHandler(contextHandler);
    contextHandler.addServletContainerInitializer(sci);
  }

  public ServerConnector configureHttpConnector(int port) {
    HttpConnectionFactory connectionFactory = new HttpConnectionFactory();

    // don't return the server version header - "because security"
    connectionFactory.getHttpConfiguration().setSendServerVersion(false);

    /* Support "x-forwarded" headers used by AWS ALB. 
    Got this from https://www.javatips.net/api/org.eclipse.jetty.server.forwardedrequestcustomizer
    */
    connectionFactory.getHttpConfiguration().
      addCustomizer(new ForwardedRequestCustomizer());
    
    ServerConnector connector = new ServerConnector(server, connectionFactory);
    connector.setPort(port);

    // avoid info leakage "servlet" name tells caller we're using java
    var errorHandler = new ErrorHandler();
    errorHandler.setShowServlet(false);
    connector.getServer().setErrorHandler(errorHandler);
    
    server.setConnectors(new Connector[]{connector});
    return connector;
  }

  public void startJoin() throws Exception {
    server.start();

    // Don't know why I'm doing this - proud member of the cargo cult
    server.join();
  }

  public void shutdown() {
    if( !server.isStarted() ){
      // e.g. could not bind listen address
      log.info("Raido shutdown requested before Jetty was started");
      return;
    }

    log.info("Raido shutdown requested, stopping Jetty server");
    server.setStopAtShutdown(true);
    try {
      server.stop();
    }
    catch( Exception e ){
      log.error("Error while stopping jetty server: " + e.getMessage(), e);
    }
  }

  public Server getServer() {
    return server;
  }
}
