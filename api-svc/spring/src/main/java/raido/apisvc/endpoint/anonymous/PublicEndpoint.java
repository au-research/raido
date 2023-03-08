package raido.apisvc.endpoint.anonymous;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PublicEndpoint {
  public static final String STATUS_PATH = "/public/status";

  /** Be careful with changes to this, it is used by the Auto-scaling group 
  health-check.
  For example, do not implement a DB connection check here - that would just
  result in the ASG cycling EC2 instances constantly, which will do nothing 
  to resolve the DB issue. */ 
  @GetMapping(STATUS_PATH)
  public Map<String, String> warmUp(){
    return Map.of("status","UP");
  }

}
