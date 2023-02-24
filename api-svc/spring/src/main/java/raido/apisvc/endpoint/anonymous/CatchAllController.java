package raido.apisvc.endpoint.anonymous;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 Handles all GET requests that are not "API endpoint" paths.
 If the controller doesn't know what to do with the path, it redirects to
 the website.
 @see raido.apisvc.spring.config.RaidWebSecurityConfig
 */
@RestController
public class CatchAllController {

  public static final String RAID_WEBSITE = "https://www.raid.org.au";

  @GetMapping("/")
  public ResponseEntity<?> catchAll(){
    
    // don't log this, will just fill up logs for no benefit  
    HttpHeaders headers = new HttpHeaders();
    headers.add("Location", RAID_WEBSITE);
    return new ResponseEntity<String>(headers, HttpStatus.FOUND);
  }

}
