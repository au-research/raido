package raido.apisvc.endpoint.anonymous;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.util.JvmUtil;

import java.util.Map;

@RestController
public class PublicEndpoint {
  public static final String STATUS_PATH = "/public/status";
  public static final Map<String, String> STATUS = Map.of("status", "UP");

  /** Be careful with changes to this, it is used by the Auto-scaling group 
  health-check.
  For example, do not implement a DB connection check here - that would just
  result in the ASG cycling EC2 instances constantly, which will do nothing 
  to resolve the DB issue. */ 
  @GetMapping(STATUS_PATH)
  public Map<String, String> warmUp(){
    JvmUtil.logMemoryInfo(STATUS_PATH);
    return STATUS;
  }

}
