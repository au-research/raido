package raido.inttest;

import org.eclipse.jetty.server.ServerConnector;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import raido.apisvc.Api;
import raido.apisvc.jetty.EmbeddedJetty;
import raido.apisvc.spring.config.ApiConfig;
import raido.apisvc.util.Log;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static raido.apisvc.util.ExceptionUtil.runtimeException;
import static raido.apisvc.util.JvmUtil.normaliseJvmDefaults;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.NetUtil.isLocalhostPortAvailable;

public class JettyTestServer 
implements BeforeAllCallback, 
  ExtensionContext.Store.CloseableResource 
{
  private static final Log log = to(JettyTestServer.class);
  
  private static boolean started = false;

  private static ServerConnector serverConnector;
  private static EmbeddedJetty jetty;
  private static AnnotationConfigWebApplicationContext rootContext;

  private Map<RequestMappingInfo, HandlerMethod> intTestApiHandlerMethods;


  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    if( !started ){
      started = true;
      initTestRaido();
      context.getRoot().getStore(GLOBAL).
        put(this.getClass().getName(), this);
    }
  }

  @Override
  public void close() {
    shutdownIntTestServer();
  }

  public void initTestRaido() throws Exception {
    to(IntegrationTestCase.class).info("initTestRaido()");
    normaliseJvmDefaults();

    jetty = new EmbeddedJetty();

    /* I often accidentally run both local dev server and func tests at same 
    time on the same DB.  Using the same http port acts as a proxy "shared 
    resource" to detect that situation. */
    if( !isLocalhostPortAvailable(Api.PORT) ){
      throw runtimeException(
        "Port %s is in use," +
          " usually caused by a Raido dev server still running." +
          " Stop other process so they don't step on each others DB.",
        Api.PORT);
    }

    serverConnector = jetty.configureHttpConnector(Api.PORT);
    jetty.addServletContainerInitializer("JettyTestServer", (sci, ctx) ->
    {
      rootContext = EmbeddedJetty.initApplicationContext(ctx, ApiConfig.class);
      MutablePropertySources propertySources =
        rootContext.getEnvironment().getPropertySources();

      rootContext.addApplicationListener(event -> {
        if( event instanceof ContextRefreshedEvent ){
          intTestApiHandlerMethods = rootContext.
            getBean(RequestMappingHandlerMapping.class).getHandlerMethods();
        }
      });
    });

    jetty.getServer().start();
  }

  public static void shutdownIntTestServer() {
    try {
      jetty.shutdown();
    }
    catch( Exception e ){
      fail("Jetty did not shutdown properly after unit tests", e);
    }
  }

  public Map<RequestMappingInfo, HandlerMethod> getIntTestApiHandlerMethods() {
    return intTestApiHandlerMethods;
  }

  public EmbeddedJetty getJetty() {
    return jetty;
  }

  /** this is the "prod" api-svc Spring context, not the inttest context */
  public AnnotationConfigWebApplicationContext getRootContext() {
    return rootContext;
  }
  
}