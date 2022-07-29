package raido.spring.config.environment;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import raido.util.Guard;
import raido.util.Log;

import static raido.util.Log.to;

@Component
public class ApidsProps {
  private static final Log log = to(ApidsProps.class);
  
  @Value("${Apids.secret:}")
  public String secret;
  
  @Value("${Apids.appId:}")
  public String appId;
  
  @Value("${Apids.serviceUrl:}")
  public String serviceUrl;
  
  @Value("${Apids.raidDomain:}")
  public String raidDomain;
  
  @PostConstruct
  public void guardValues(){
    Guard.allHaveValue("must set prop values", 
      secret, appId, serviceUrl, raidDomain);
  }
}
