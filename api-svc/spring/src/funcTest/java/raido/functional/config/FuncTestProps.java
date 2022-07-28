package raido.functional.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FuncTestProps {
  @Value("${raidoArdcLiveToken:}")
  public String raidoArdcLiveToken;

  /**
   This is the location of the turnip API server to test against
   */
  @Value("${raidoApiServer:localhost:8080}")
  public String raidoApiServer;

  /**
   HTTP is ok for localhost
   */
  @Value("${raidoApiProtocol:http}")
  public String raidoApiProtocol;
}
