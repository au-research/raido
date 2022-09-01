package raido.apisvc.endpoint.raidv1;

import jakarta.servlet.http.HttpServletRequest;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.service.apids.ApidsService;
import raido.apisvc.service.apids.model.ApidsMintResponse;
import raido.apisvc.spring.config.RaidWebSecurityConfig;
import raido.apisvc.spring.security.raidv1.Raid1PostAuthenicationJsonWebToken;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;
import raido.db.jooq.raid_v1_import.tables.records.RaidRecord;
import raido.idl.raidv1.api.RaidV1Api;
import raido.idl.raidv1.model.RaidCreateModel;
import raido.idl.raidv1.model.RaidModel;
import raido.idl.raidv1.model.RaidModelMeta;
import raido.idl.raidv1.model.RaidPublicModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.List.of;
import static org.eclipse.jetty.http.HttpStatus.BAD_REQUEST_400;
import static org.eclipse.jetty.http.HttpStatus.NOT_FOUND_404;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static raido.apisvc.endpoint.message.RaidApiV1Message.DEMO_NOT_SUPPPORTED;
import static raido.apisvc.endpoint.message.RaidApiV1Message.HANDLE_NOT_FOUND;
import static raido.apisvc.endpoint.message.RaidApiV1Message.MINT_DATA_ERROR;
import static raido.apisvc.spring.config.RaidWebSecurityConfig.RAID_V1_API;
import static raido.apisvc.spring.security.ApiSafeException.apiSafe;
import static raido.apisvc.util.DateUtil.formatDynamoDateTime;
import static raido.apisvc.util.DateUtil.formatRaidV1DateTime;
import static raido.apisvc.util.DateUtil.parseDynamoDateTime;
import static raido.apisvc.util.ExceptionUtil.iae;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.isTrue;
import static raido.apisvc.util.RestUtil.urlDecode;
import static raido.apisvc.util.StringUtil.hasValue;
import static raido.apisvc.util.StringUtil.isBlank;
import static raido.db.jooq.raid_v1_import.tables.Raid.RAID;

/* without the proxy mode setting, Spring doesn't see the requestmappings from 
the interface and so won't define our RaidV1 endpoints */
@Scope(proxyMode = TARGET_CLASS)
@RequestMapping(RAID_V1_API)
@RestController
public class RaidV1 implements RaidV1Api {
  public static final String HANDLE_URL_PREFIX = "/handle";
  public static final String HANDLE_CATCHALL_PREFIX =
    RAID_V1_API + HANDLE_URL_PREFIX + "/";
  public static final String HANDLE_SEPERATOR = "/";
  
  public static final JSONB NON_EXPORTED_MARKER_VALUE = JSONB.valueOf("{}");
  
  private static final Log log = to(RaidV1.class);

  private ApidsService apidsSvc;
  private DSLContext db;

  public RaidV1(ApidsService apidsSvc, DSLContext db) {
    this.apidsSvc = apidsSvc;
    this.db = db;
  }

  /**
   For testing, c.f. declaring a method parameter (I can't figure out
   how to get openapi to generate code like that).
   */
  public Raid1PostAuthenicationJsonWebToken getAuthentication() {
    return Guard.isInstance(
      Raid1PostAuthenicationJsonWebToken.class,
      getContext().getAuthentication());
  }

  /**
   This method catches "encoded" handle slashes.
   Watch out - handles have slashes in them, by definition 😢
   Currently, API clients encode the handle slash as `%2f` - but that triggers
   the default Spring HttpStrictFirewall.
   We've disabled that in {@link RaidWebSecurityConfig}, which is a risk.
   V2 API should always pass handles as params instead of in the path?
   @see RaidWebSecurityConfig#allowUrlEncodedSlashHttpFirewall
   @see #handleCatchAll(HttpServletRequest, Boolean) 
   */
  public RaidPublicModel handleRaidIdGet(
    String raidId,
    Boolean demo
  ) {
    Guard.hasValue("raidId must have a value", raidId);
    guardDemoEnv(demo);

    RaidPublicModel result = db.select().
      from(RAID).
      where(RAID.HANDLE.eq(raidId)).
      fetchOneInto(RaidPublicModel.class);
    if( result == null ){
      throw apiSafe(HANDLE_NOT_FOUND, NOT_FOUND_404, of(raidId));
    }
    return result;
  }

  public void guardDemoEnv(Boolean demo) {
    if( isTrue(demo) ){
      throw apiSafe(DEMO_NOT_SUPPPORTED, BAD_REQUEST_400);
    }
  }

  /**
   This method catches all prefixes with path prefix `/v1/handle` and attempts
   to parse the parameter manually, so that we can receive handles that are
   just formatted with simple slashes.
   The openapi spec is defined with the "{raidId}' path param because it makes
   it more clear to the caller what the url is expected to look like.
   <p>
   IMPROVE: should write some detailed/edgecase unit tests for this
   path parsing logic.
   */
  @RequestMapping(
    method = RequestMethod.GET,
    value = HANDLE_URL_PREFIX + "/**",
    produces = {"application/json"}
  )
  public RaidPublicModel handleCatchAll(
    HttpServletRequest req,
    @RequestParam(value = "demo", required = false) Boolean demo
  ) {
    String path = urlDecode(req.getServletPath().trim());
    log.with("path", req.getServletPath()).
      with("decodedPath", path).
      with("params", req.getParameterMap()). 
      info("handleCatchAll() called");

    if( !path.startsWith(HANDLE_CATCHALL_PREFIX) ){
      throw iae("unexpected path: %s", path);
    }
    
    String handle = path.substring(HANDLE_CATCHALL_PREFIX.length());
    if( !handle.contains(HANDLE_SEPERATOR) ){
      throw apiSafe("handle did not contain a slash character", 
        BAD_REQUEST_400, of(handle));
    }

    return handleRaidIdGet(handle, demo);
  }

  @Transactional
  public RaidModel raidPost(RaidCreateModel req) {
    var identity = getAuthentication();
    guardV1MintInput(req);
    populateMintDefaultValues(req, identity.getName());

    /* Do not hold TX open across this, it takes SECONDS.
    Note that security stuff (i.e. to populate `identity`) happens under its
    own TX, so no need to worry about that. */
    ApidsMintResponse apidsHandle = 
      apidsSvc.mintApidsHandle(req.getContentPath());

    // everything above this point needs to be non-transactional
    RaidRecord record = db.newRecord(RAID).
      setHandle(apidsHandle.identifier.handle).
      setOwner(identity.getName()).
      setContentPath(req.getContentPath()).
      setContentIndex(apidsHandle.identifier.property.index.toString()).
      setName(req.getMeta().getName()).
      setDescription(req.getMeta().getDescription()).
      setStartDate(parseDynamoDateTime(req.getStartDate())).
      setCreationDate(LocalDateTime.now()).
      setS3Export(NON_EXPORTED_MARKER_VALUE);
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
        description(record.getDescription())).
      providers(emptyList()).
      institutions(emptyList());
  }

  private void populateMintDefaultValues(RaidCreateModel create, String owner) {
    if( hasValue(create.getStartDate()) ){
      // just leave it alone for the moment, maybe add to the guard method 
      // to check the format
    }
    else {
      create.setStartDate(formatDynamoDateTime(LocalDateTime.now()));
    }

    // meta is expected to be guarded, should net get nulls here
    if( isBlank(create.getMeta().getDescription()) ){
      /* current time in sydney is dodgy.
      Doesn't even work for most of Aus, let alone Oceania and people are
      allowed to mint from other timezoness too! */
      create.getMeta().setDescription(
        "RAiD created by '%s' at '%s'".formatted(
          owner, formatRaidV1DateTime(LocalDateTime.now()) ));
    }

  }

  public void guardV1MintInput(RaidCreateModel create) {
    List<String> problems = new ArrayList<>();
    if( !hasValue(create.getContentPath()) ){
      problems.add("no 'contentPath' provided");
    }

    if( create.getMeta() == null ){
      problems.add("no 'meta' provided");
      throw apiSafe(MINT_DATA_ERROR, BAD_REQUEST_400, problems);
    }
    
    /* the  fake name generation logic has been removed - makes no sense
    v2 UI will use v2 API and will create in one step.  
    Legacy API users are expected to be sending a name. */
    if( !hasValue(create.getMeta().getName()) ){
      problems.add("no 'meta.name' provided");
    }
    
    if( !problems.isEmpty() ){
      throw apiSafe(MINT_DATA_ERROR, BAD_REQUEST_400, problems);
    }
  }

}
