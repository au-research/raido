package raido.functional;

import org.eclipse.jetty.server.ServerConnector;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import raido.Api;
import raido.jetty.EmbeddedJetty;
import raido.spring.config.ApiConfig;
import raido.util.Log;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static raido.util.ExceptionUtil.createRuntimeException;
import static raido.util.JvmUtil.normaliseJvmDefaults;
import static raido.util.Log.to;
import static raido.util.NetUtil.isLocalhostPortAvailable;

public class JettyTestServer 
implements BeforeAllCallback, 
  ExtensionContext.Store.CloseableResource 
{
  private static final Log log = to(JettyTestServer.class);
  
  private static boolean started = false;

  private static ServerConnector serverConnector;
  private static EmbeddedJetty jetty;
  
  private Map<RequestMappingInfo, HandlerMethod> turnipApiHandlerMethods;


  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    if( !started ){
      started = true;
      initTurnip();
      context.getRoot().getStore(GLOBAL).
        put(this.getClass().getName(), this);
    }
  }

  @Override
  public void close() {
    shutdownTurnip();
  }

  public void initTurnip() throws Exception {
    to(FunctionalTestCase.class).info("initTurnip()");
    normaliseJvmDefaults();

    jetty = new EmbeddedJetty();

    /* I often accidentally run both local dev server and func tests at same 
    time on the same DB.  Using the same http port acts as a proxy "shared 
    resource" to detect that situation. */
    if( !isLocalhostPortAvailable(Api.PORT) ){
      throw createRuntimeException(
        "Port %s is in use," +
          " usually caused by a Raido dev server still running." +
          " Stop other process so they don't step on each others DB.",
        Api.PORT);
    }

    serverConnector = jetty.configureHttpConnector(Api.PORT);
    jetty.addServletContainerInitializer((sci, ctx) ->
    {
      var rootContext = ApiConfig.initServletContext(ctx);
      MutablePropertySources propertySources =
        rootContext.getEnvironment().getPropertySources();

      rootContext.addApplicationListener(event -> {
        if( event instanceof ContextRefreshedEvent ){
          turnipApiHandlerMethods = rootContext.
            getBean(RequestMappingHandlerMapping.class).getHandlerMethods();
        }
      });
    });

    jetty.getServer().start();
  }

  public static void shutdownTurnip() {
    try {
      jetty.shutdown();
    }
    catch( Exception e ){
      fail("Jetty did not shutdown properly after unit tests", e);
    }
  }

  public Map<RequestMappingInfo, HandlerMethod> getTurnipApiHandlerMethods() {
    return turnipApiHandlerMethods;
  }

  public EmbeddedJetty getJetty() {
    return jetty;
  }
}