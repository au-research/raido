package raido.endpoint.raidv1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raido.service.apids.ApidsService;

import java.util.Map;

import static raido.spring.config.RaidV1SecurityConfig.RAID_V1_API;

@RequestMapping(RAID_V1_API)
@RestController
public class RaidV1Endpoint {

  private ApidsService apidsSvc;

  public RaidV1Endpoint(ApidsService apidsSvc) {
    this.apidsSvc = apidsSvc;
  }

  /**  Just for initial testing of security */
  @GetMapping("/raid/ping")
  public Map<String, String> raidPing(){
    return Map.of("status","UP");
  }

  @PostMapping("/raid/minttest")
  public Map<String, String> raidMintTest(){
    apidsSvc.createAndsHandle();
    return Map.of("status","UP");
  }

}
