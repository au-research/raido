package raido.apisvc.endpoint.raidv1;

import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.service.apids.ApidsService;
import raido.apisvc.service.apids.model.ApidsMintResponse;
import raido.apisvc.spring.config.RaidV1WebSecurityConfig;
import raido.apisvc.spring.security.ApiSafeException;
import raido.apisvc.spring.security.raidv1.Raid1PostAuthenicationJsonWebToken;
import raido.apisvc.util.Log;
import raido.db.jooq.raid_v1_import.tables.records.RaidRecord;
import raido.idl.raidv1.model.RaidCreateModel;
import raido.idl.raidv1.model.RaidCreateModelMeta;
import raido.idl.raidv1.model.RaidModel;
import raido.idl.raidv1.model.RaidModelMeta;
import raido.idl.raidv1.model.RaidPublicModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.List.of;
import static org.eclipse.jetty.http.HttpStatus.BAD_REQUEST_400;
import static org.eclipse.jetty.http.HttpStatus.NOT_FOUND_404;
import static raido.apisvc.endpoint.message.RaidApiV1Message.DEMO_NOT_SUPPPORTED;
import static raido.apisvc.endpoint.message.RaidApiV1Message.HANDLE_NOT_FOUND;
import static raido.apisvc.endpoint.message.RaidApiV1Message.MINT_DATA_ERROR;
import static raido.apisvc.spring.config.RaidV1WebSecurityConfig.RAID_V1_API;
import static raido.apisvc.util.DateUtil.formatDynamoDateTime;
import static raido.apisvc.util.DateUtil.parseDynamoDateTime;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.isTrue;
import static raido.apisvc.util.StringUtil.hasValue;
import static raido.apisvc.util.StringUtil.isBlank;
import static raido.db.jooq.raid_v1_import.tables.Raid.RAID;

@RequestMapping(RAID_V1_API)
@RestController
public class RaidV1 {
  public static final String HANDLE_URL_PREFIX = "/handle";
  private static final Log log = to(RaidV1.class);

  private ApidsService apidsSvc;
  private DSLContext db;

  public RaidV1(ApidsService apidsSvc, DSLContext db) {
    this.apidsSvc = apidsSvc;
    this.db = db;
  }

  /** Watch out - handles have slashes in them, by definition 😢
   Currently, API clients encode the handle slash as `%2f` - but that triggers 
   the default Spring HttpStrictFirewall.
   We've disabled that in {@link RaidV1WebSecurityConfig}, which is a risk. 
   V2 API should always pass handles as params instead of in the path?
   Or... we could catch this request with path = /v1/handle/** and parse out 
   the handle ourselves?  That would allow us to fix the security risk, and
   let users just use normal urls.  But if we want to do backwards compat. 
   with encoded url slashes, we'll still have to have the firewall rule 
   disabled.

   @see RaidV1WebSecurityConfig#allowUrlEncodedSlashHttpFirewall
   */
  @GetMapping(HANDLE_URL_PREFIX + "/{raidId}")
  public RaidPublicModel getRaid(
    @PathVariable("raidId") String raidId,
    @RequestParam(value = "demo", required = false) Optional<Boolean> demo
  ){
    guardDemoEnv(demo);
    
    RaidPublicModel result = db.select().
      from(RAID).
      where(RAID.HANDLE.eq(raidId)).
      fetchOneInto(RaidPublicModel.class);
    if( result == null ){
      throw new ApiSafeException(HANDLE_NOT_FOUND, NOT_FOUND_404, of(raidId));
    }
    return result;
  }
  
  public void guardDemoEnv(Optional<Boolean> demo){
    if( isTrue(demo) ){
      throw new ApiSafeException(DEMO_NOT_SUPPPORTED, BAD_REQUEST_400);
    }
  }
  
  @PostMapping("/raid")
  @Transactional
  public RaidModel raidMint(
    Raid1PostAuthenicationJsonWebToken identity,
    @RequestBody RaidCreateModel create
  ){
    guardV1MintInput(create);

    populateDefaultValues(create);

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
      create.setMeta(new RaidCreateModelMeta());
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
      throw new ApiSafeException(MINT_DATA_ERROR, problems);
    }
  } 
  
}
