package raido.apisvc.spring.config.environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import raido.apisvc.util.Log;

import static raido.apisvc.util.Log.to;

@Component
public class ApidsProps {
  private static final Log log = to(ApidsProps.class);

  @Value("${Apids.secret:}")
  public String secret;
  
  @Value("${Apids.appId:57158ed6a84b87b23b23cbf3016d59786fb2de5a}")
  public String appId;
  
  @Value("${Apids.serviceUrl:https://demo.identifiers.ardc.edu.au/pids}")
  public String serviceUrl;
  
  // IMPROVE: change to true, so default development environments use it?
  @Value("${Apids.inMemoryStub:false}")
  public boolean inMemoryStub;

  @Value("${Apids.inMemoryStubDelay:150}")
  public long inMemoryStubDelay;

}
