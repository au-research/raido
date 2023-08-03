package raido.apisvc.jetty;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletRegistration;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import raido.apisvc.spring.RequestLoggingFilter;
import raido.apisvc.util.Log;

import static java.util.Collections.emptySet;
import static raido.apisvc.util.Log.to;

/*
IMPROVE: enable compression at HTTP level, I think it's something like:
contextHandler.insertHandler(new GzipHandler());
 */
public class EmbeddedJetty {

  public static final String DISPATCHER_NAME = "raido_dispatcher";

  private static final Log log = to(EmbeddedJetty.class);

  private final Server server;

  public EmbeddedJetty() {
    QueuedThreadPool qtp = new QueuedThreadPool();
    // by default its "qtp${hashcode}", I like this better
    qtp.setName("Jetty");
    server = new Server(qtp);
  }

  public void addServletContainerInitializer(
    String name,
    ServletContainerInitializer sci
  ) {
    ServletContextHandler contextHandler = new ServletContextHandler();
    server.setHandler(contextHandler);
    contextHandler.addServletContainerInitializer(sci);
    contextHandler.setDisplayName(name);
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


  public static AnnotationConfigWebApplicationContext initApplicationContext(
    ServletContext ctx, 
    Class<?> configurationClass
  ) {
    log.with("contextName", ctx.getServletContextName()).
      with("contextPath", ctx.getContextPath()).
      info("initServletContext()");
    // Create the 'root' Spring application context
    AnnotationConfigWebApplicationContext rootContext =
      new AnnotationConfigWebApplicationContext();
    rootContext.setDisplayName("Root context - " +
      configurationClass.getSimpleName());
    rootContext.register(configurationClass);
    // Manage the lifecycle of the root application context
    ctx.addListener(new ContextLoaderListener(rootContext){
      @Override
      public void contextInitialized(ServletContextEvent event) {
        log.with("event", event).info("contextInitialized()");
        super.contextInitialized(event);
      }

      @Override
      public void contextDestroyed(ServletContextEvent event) {
        log.with("event", event).info("contextDestroyed()");
        super.contextDestroyed(event);
      }
    });

    // probs not necessary if Spring http config is set to STATELESS 
    ctx.setSessionTrackingModes(emptySet());

    /* Register and map the dispatcher servlet
     Example code used a separate Spring context of the servlet, but I don't
     see why that's necessary. */
    DispatcherServlet servlet = new DispatcherServlet(rootContext);
    // Make sure NoHandlerFound is handled by custom HandlerExceptionResolver
    servlet.setThrowExceptionIfNoHandlerFound(true);
    ServletRegistration.Dynamic dispatcher =
      ctx.addServlet(DISPATCHER_NAME, servlet);
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping("/");

    RequestLoggingFilter.add(ctx, DISPATCHER_NAME);
    
    /* Dunno why, but Spring doesn't find WebApplicationInitializer 
    interfaces automatically, so we have to call onStartup() directly. 
    */
    new AbstractSecurityWebApplicationInitializer(){}.onStartup(ctx);

    return rootContext;
  }  
}
