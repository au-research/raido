package raido.apisvc.spring.config.environment;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;

import static raido.apisvc.util.Log.to;

@Component
public class ApidsProps {
  private static final Log log = to(ApidsProps.class);

  @Value("${Apids.secret:}")
  public String secret;
  
  @Value("${Apids.appId:57158ed6a84b87b23b23cbf3016d59786fb2de5a}")
  public String appId;
  
  @Value("${Apids.serviceUrl:https://demo.ands.org.au:8443/pids/mint?type=URL&value=https://www.raid.org.au/}")
  public String serviceUrl;
  
  @PostConstruct
  public void guardValues(){
    Guard.allHaveValue("must set ApidsProps values", 
      secret, appId, serviceUrl);
  }
}
