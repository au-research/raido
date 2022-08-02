package raido.endpoint.raidv1;

import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebInputException;
import raido.endpoint.message.ApiMessage;
import raido.idl.raidv1.model.RaidCreateModel;
import raido.idl.raidv1.model.RaidModel;
import raido.service.apids.ApidsService;
import raido.service.apids.model.ApidsMintResponse;
import raido.spring.security.ApiClientException;
import raido.spring.security.ApiSafeException;
import raido.spring.security.raidv1.Raid1PostAuthenicationJsonWebToken;
import raido.util.DateUtil;
import raido.util.Log;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static raido.endpoint.message.ApiMessage.RAID_V1_MINT_DATA_ERROR;
import static raido.spring.config.RaidV1SecurityConfig.RAID_V1_API;
import static raido.util.Log.to;
import static raido.util.StringUtil.hasValue;

@RequestMapping(RAID_V1_API)
@RestController
public class RaidV1 {
  private static final Log log = to(RaidV1.class);
  private static ZoneId sydneyZone = ZoneId.of("Australia/Sydney");
  
  private ApidsService apidsSvc;

  public RaidV1(ApidsService apidsSvc) {
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
  // todo:sto this is wrong, just want to verify
  @Transactional
  public RaidModel raidMint(
    Raid1PostAuthenicationJsonWebToken identity,
    @RequestBody RaidCreateModel create
  ){
    var problems = checkV1Mint(create);
    if( !problems.isEmpty() ){
      log.with("problems", problems).error("problem minting raid for V1 api");
      throw new ApiClientException(RAID_V1_MINT_DATA_ERROR, problems);
    }
    if( hasValue(create.getStartDate()) ){
      // just leave it alone for the moment, maybe add to the check method
    }
    else {
      create.setStartDate(formatDynamoDateTime(LocalDateTime.now()));
    }
    
    
    // do not hold TX open across this, it takes SECONDS 
//    ApidsMintResponse handle = apidsSvc.mintApidsHandle();
//    handle = apidsSvc.mintApidsHandle();
    var result = new RaidModel();
    result.setHandle("sto/test.1");
    return result;
  }

  public List<String> checkV1Mint(RaidCreateModel create){
    List<String> problems = new ArrayList<>();
    if( !hasValue(create.getContentPath()) ){
      problems.add("no contentPath provided");
    }
    return problems;
  } 
  
  public static String formatDynamoDateTime(LocalDateTime d){
    return DateUtil.formatDateTime(DateUtil.ISO_SECONDS_FORMAT, sydneyZone, d);
  }
  
}
