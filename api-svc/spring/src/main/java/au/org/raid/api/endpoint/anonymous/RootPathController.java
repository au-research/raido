package au.org.raid.api.endpoint.anonymous;

import au.org.raid.api.endpoint.raidv2.PublicStable;
import au.org.raid.api.service.raid.id.IdentifierHandle;
import au.org.raid.api.service.raid.id.IdentifierParser;
import au.org.raid.api.spring.config.environment.EnvironmentProps;
import au.org.raid.api.util.Log;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static au.org.raid.api.spring.config.RaidWebSecurityConfig.ROOT_PATH;
import static au.org.raid.api.util.Log.to;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

@RestController
public class RootPathController {
  private static final Log log = to(RootPathController.class);
  public static final String ROOT_CATCHALL_PATTERN = ROOT_PATH + "**";
  public static final String HANDLE_PATH_PATTERN = "{prefix}/{suffix}";

  private EnvironmentProps env;
  private IdentifierParser parser;
  private PublicStable publicApi;
  
  public RootPathController(
    EnvironmentProps env,
    IdentifierParser parser,
    PublicStable publicApi
  ) {
    this.env = env;
    this.parser = parser;
    this.publicApi = publicApi;
  }

  @GetMapping(value = ROOT_PATH, produces = TEXT_HTML_VALUE)
  public ResponseEntity<Void> catchAllBrowserViewRoot(){
    return ResponseEntity.status(FOUND).
      header(LOCATION, env.rootPathRedirect).body(null);  
  }
  
  /* Without this, "GET / Accept:application/json" would return a 406 instead
   of 404, don't think it really matters much */
  @GetMapping(value = ROOT_PATH, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> catchAllApiGetRoot(){
    throw new ResponseStatusException(NOT_FOUND);
  }

  /* Without this, `GET /prefix%2Fsuffix Accept:application/json` would 
  return 404, because it wouldn't match the OpenAPI generated 
  `/{prefix}/{suffix}` pattern. */
  @GetMapping(value = ROOT_CATCHALL_PATTERN, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<?> catchAllApiGetHandle(HttpServletRequest req){
    var parse = parser.parseHandle(req.getServletPath());
    
    if( parse instanceof IdentifierHandle handle ){
      return ResponseEntity.ok(
        publicApi.publicApiGetRaid(
          handle.prefix(),
          handle.suffix() ));
    }
    
    throw new ResponseStatusException(NOT_FOUND);
  }

  /**
   This is done manually instead of via OpenAPI, because:
   (A) openapi can't generate two mapping differentiated only by the "content"
   key
   (B) it's not really part of the "API", we don't expect/want integrators
   to be using the HTML directly (besides, we render via React, so it would 
   likely be a huge pain for them to use). They should call the API version and
   deal with the JSON.
   (C) we need to parse the handle ourselves to deal with URL encoded handles 
   */
  @GetMapping(
    value = ROOT_CATCHALL_PATTERN,
    produces = {TEXT_HTML_VALUE}
  )
  public ResponseEntity<Void> publicBrowserViewRaid(
    HttpServletRequest req
  ) {
    var parse = parser.parseHandle(req.getServletPath());

    if( parse instanceof IdentifierHandle handle ){
      return ResponseEntity.status(FOUND).
        header(LOCATION, handle.format(env.raidoLandingPage)).
        build();
    }
    
    throw new ResponseStatusException(NOT_FOUND);
  }

  /** This was implemented because a browser request from an empty browser
  with no Accept header was being processed by the PublicStable get-raid 
  endpoint pattern instead of the TEXT_HTML patterns.  
   */
  @GetMapping(value = ROOT_PATH + HANDLE_PATH_PATTERN)
  public ResponseEntity<Void> publicBrowserViewRaid(
    @PathVariable("prefix") String prefix,
    @PathVariable("suffix") String suffix
  ) {
    IdentifierHandle handle = new IdentifierHandle(prefix, suffix);
    return ResponseEntity.status(FOUND).
      header(LOCATION, handle.format(env.raidoLandingPage)).
      build();
  }
  
}
