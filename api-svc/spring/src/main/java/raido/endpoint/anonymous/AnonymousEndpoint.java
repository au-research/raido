package raido.endpoint.anonymous;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static raido.spring.config.WebSecurityConfig.PUBLIC;

@RequestMapping(PUBLIC)
@RestController
public class AnonymousEndpoint {

  @GetMapping("/status")
  public Map<String, String> warmUp(){
    return Map.of("status","UP");
  }

}
