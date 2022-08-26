package raido.apisvc.endpoint.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import raido.apisvc.spring.config.environment.AafOidcProps;
import raido.apisvc.util.Log;

import java.io.IOException;

import static raido.apisvc.util.Log.to;


@RequestMapping
@RestController
public class AuthnEndpoint {
  public static final String IDP_URL = "/idpresponse";

  private static final Log log = to(AuthnEndpoint.class);

  private AafOidcProps aaf;
  private RestTemplate rest;

  public AuthnEndpoint(AafOidcProps aaf, RestTemplate rest) {
    this.aaf = aaf;
    this.rest = rest;
  }

  @GetMapping(IDP_URL)
  public void authenticate(
    HttpServletRequest req, HttpServletResponse res
  ) throws IOException {
    log.with("paramMap", req.getParameterMap()).info("/idprequest");

    String idpResponseCode = req.getParameter("code");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("code", idpResponseCode);
    map.add("client_id", aaf.clientId);
    map.add("client_secret", aaf.clientSecret);
    map.add("grant_type", "authorization_code");
    map.add("redirect_uri", aaf.redirectUri);

    HttpEntity<MultiValueMap<String, String>> request =
      new HttpEntity<>(map, headers);

    log.with("bod", request.getBody()).info();
    
    ResponseEntity<String> response = rest.postForEntity(
      aaf.tokenUrl, request, String.class);

    log.with("response", response).
      with("response.body", response.getBody()).
      info("aaf response");

    res.sendRedirect("client-redirect#id_token=%s".formatted("xxx.yyy.zzz"));
  }


}
