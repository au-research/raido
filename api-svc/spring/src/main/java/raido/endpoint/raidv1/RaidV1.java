package raido.endpoint.raidv1;

import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raido.db.jooq.raid_v1_import.tables.records.RaidRecord;
import raido.idl.raidv1.model.RaidCreateModel;
import raido.idl.raidv1.model.RaidModel;
import raido.idl.raidv1.model.RaidModelMeta;
import raido.service.apids.ApidsService;
import raido.service.apids.model.ApidsMintResponse;
import raido.spring.security.ApiSafeException;
import raido.spring.security.raidv1.Raid1PostAuthenicationJsonWebToken;
import raido.util.Log;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static raido.db.jooq.raid_v1_import.tables.Raid.RAID;
import static raido.endpoint.message.ApiMessage.RAID_V1_MINT_DATA_ERROR;
import static raido.spring.config.RaidV1WebSecurityConfig.RAID_V1_API;
import static raido.util.DateUtil.formatDynamoDateTime;
import static raido.util.DateUtil.parseDynamoDateTime;
import static raido.util.Log.to;
import static raido.util.StringUtil.hasValue;
import static raido.util.StringUtil.isBlank;

@RequestMapping(RAID_V1_API)
@RestController
public class RaidV1 {
  private static final Log log = to(RaidV1.class);
  
  private ApidsService apidsSvc;
  private DSLContext db;
  private JdbcTemplate jdbcTemplate;
  
  public RaidV1(ApidsService apidsSvc, DSLContext db, JdbcTemplate jdbcTemplate) {
    this.apidsSvc = apidsSvc;
    this.db = db;
    this.jdbcTemplate = jdbcTemplate;
  }

  /**  Just for initial testing of security */
  @GetMapping("/raid/ping")
  public Map<String, String> raidPing(){
    return Map.of("status","UP");
  }

  @PostMapping("/raid/apidstest")
  public Map<String, String> raidMintTest(){
    // do not hold TX open across this, it takes SECONDS 
    ApidsMintResponse handle = apidsSvc.mintApidsHandle();
    handle = apidsSvc.mintApidsHandle();
    return Map.of("status","UP");
  }

  @PostMapping("/raid/mint")
  @Transactional
  public RaidModel raidMint(
    Raid1PostAuthenicationJsonWebToken identity,
    @RequestBody RaidCreateModel create
  ){
    guardV1MintInput(create);

    populateDefaultValues(create);

//    jdbcTemplate.update(
//      "INSERT INTO api_svc.test_table values (?)",
//      "sto/test."+System.currentTimeMillis()
//    );
//    return new RaidModel();
    
    /* Do not hold TX open across this, it takes SECONDS.
    Note that security stuff (i.e. to populate `identity`) happens under its
    own TX, so no need to worry about that. */
    ApidsMintResponse apidsHandle = apidsSvc.mintApidsHandle();
    
    // everything above this point needs to be non-transactional
    RaidRecord record = db.newRecord(RAID).
      setHandle(apidsHandle.identifier.handle).
      setOwner(identity.getName()).
      setContentPath(create.getContentPath()).
      setContentIndex(apidsHandle.identifier.property.index.toString()).
      setName(create.getMeta().getName()).
      setDescription(create.getMeta().getDescription()).
      setStartDate(parseDynamoDateTime(create.getStartDate())).
      setCreationDate(LocalDateTime.now()).
      setS3Export(JSONB.valueOf("{}"));
    record.insert();

    return new RaidModel().
      handle(record.getHandle()).
      owner(record.getOwner()).
      contentPath(record.getContentPath()).
      contentIndex(record.getContentIndex()).
      startDate(formatDynamoDateTime(record.getStartDate())).
      creationDate(formatDynamoDateTime(record.getCreationDate())).
      meta(new RaidModelMeta().
        name(record.getName()).
        description(record.getDescription()) ).
      providers( emptyList() ).
      institutions( emptyList() );
  }

  private void populateDefaultValues(RaidCreateModel create) {
    if( hasValue(create.getStartDate()) ){
      // just leave it alone for the moment, maybe add to the guard method 
      // to check the format
    }
    else {
      create.setStartDate(formatDynamoDateTime(LocalDateTime.now()));
    }

    if( create.getMeta() == null ){
      create.setMeta(new RaidModelMeta());
    }

    if( isBlank(create.getMeta().getName()) ){
      create.getMeta().setName("todo:sto name");
    }
    if( isBlank(create.getMeta().getDescription()) ){
      create.getMeta().setDescription("todo:sto descriiption");
    }
    
  }

  public void guardV1MintInput(RaidCreateModel create){
    List<String> problems = new ArrayList<>();
    if( !hasValue(create.getContentPath()) ){
      problems.add("no contentPath provided");
    }
    if( !problems.isEmpty() ){
      throw new ApiSafeException(RAID_V1_MINT_DATA_ERROR, problems);
    }
  } 
  
}
