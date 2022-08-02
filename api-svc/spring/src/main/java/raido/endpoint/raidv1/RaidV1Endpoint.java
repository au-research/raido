package raido.endpoint.raidv1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raido.idl.raidv1.model.RaidCreateModel;
import raido.service.apids.ApidsService;
import raido.service.apids.model.ApidsMintResponse;
import raido.spring.security.raidv1.Raid1PostAuthenicationJsonWebToken;
import raido.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static raido.spring.config.RaidV1SecurityConfig.RAID_V1_API;
import static raido.util.Log.to;

@RequestMapping(RAID_V1_API)
@RestController
public class RaidV1Endpoint {
  private static final Log log = to(RaidV1Endpoint.class);
  
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
    // do not hold TX open across this, it takes SECONDS 
    ApidsMintResponse handle = apidsSvc.mintApidsHandle();
    handle = apidsSvc.mintApidsHandle();
    return Map.of("status","UP");
  }

  @PostMapping("/raid/mint")
  public Map<String, String> raidMint(
    Raid1PostAuthenicationJsonWebToken identity,
    @RequestBody RaidCreateModel request
  ){
    log.with("identity", identity).
      with("request", request).info("mint called");
    
    
    
    // do not hold TX open across this, it takes SECONDS 
//    ApidsMintResponse handle = apidsSvc.mintApidsHandle();
//    handle = apidsSvc.mintApidsHandle();
    return Map.of("status","UP");
  }

  
  public List<String> checkV1Mint(){
    List<String> problems = new ArrayList<>();
    return problems;
  } 
}
