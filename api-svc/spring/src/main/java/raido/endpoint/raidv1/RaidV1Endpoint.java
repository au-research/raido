package raido.endpoint.raidv1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static raido.spring.config.WebSecurityConfig.RAID_V1_API;

@RequestMapping(RAID_V1_API)
@RestController
public class RaidV1Endpoint {


  /**  Just for initial testing of security */
  @GetMapping("/raid/ping")
  public Map<String, String> raidPing(){
    return Map.of("status","UP");
  }

}
