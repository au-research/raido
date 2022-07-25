package raido.spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import raido.util.JvmUtil;
import raido.util.Log;

import static raido.util.Log.to;

@Component
public class StartupListener implements
  ApplicationListener<ContextRefreshedEvent> {
  
  private static final Log log = to(StartupListener.class);

  @Value("${raido.greeting:no greeting config}")
  private String greeting;
  
  @Override public void onApplicationEvent(ContextRefreshedEvent event) {
    log.info("%s - %s", event.getSource().toString(), greeting);
    JvmUtil.logStartupInfo();
  }
}