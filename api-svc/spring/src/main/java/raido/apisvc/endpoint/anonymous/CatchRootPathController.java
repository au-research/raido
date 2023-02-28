package raido.apisvc.endpoint.anonymous;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import raido.apisvc.spring.config.environment.EnvironmentProps;
import raido.apisvc.util.Log;

import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static raido.apisvc.spring.config.RaidWebSecurityConfig.ROOT_PATH;
import static raido.apisvc.util.Log.to;

@RestController
public class CatchRootPathController {
  private static final Log log = to(CatchRootPathController.class);

  private EnvironmentProps env;
  
  public CatchRootPathController(
    EnvironmentProps env
  ) {
    this.env = env;
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

}
