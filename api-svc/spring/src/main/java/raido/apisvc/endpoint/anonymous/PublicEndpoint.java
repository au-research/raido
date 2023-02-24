package raido.apisvc.endpoint.anonymous;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static raido.apisvc.spring.config.RaidWebSecurityConfig.PUBLIC;

@RequestMapping(PUBLIC)
@RestController
public class PublicEndpoint {

  /** Be careful with changes to this, it is used by the Auto-scaling group 
  health-check.
  For example, do not implement a DB connection check here - that would just
  result in the ASG cycling EC2 instances constantly, which will do nothing 
  to resolve the DB issue. */ 
  @GetMapping("/status")
  public Map<String, String> warmUp(){
    return Map.of("status","UP");
  }

}
