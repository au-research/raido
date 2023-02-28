package raido.apisvc.endpoint.raidv2;

import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import raido.apisvc.spring.config.environment.EnvironmentProps;
import raido.idl.raidv2.api.PublicStableApi;
import raido.idl.raidv2.model.PublicReadRaidResponseV3;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static raido.apisvc.util.StringUtil.hasValue;

@Scope(proxyMode = TARGET_CLASS)
@RestController
@Transactional
public class PublicStable implements PublicStableApi {

  private PublicExperimental publicApi;
  private EnvironmentProps env;

  public PublicStable(PublicExperimental publicApi, EnvironmentProps env) {
    this.publicApi = publicApi;
    this.env = env;
  }

  @Override
  public PublicReadRaidResponseV3 publicApiGetRaid(
    String prefix,
    String suffix
  ) {
    if( !hasValue(prefix) || !hasValue(suffix) ){
      /* Not sure about this yet, might want it to redirect to website? */
      throw new ResponseStatusException(NOT_FOUND);
    }

    try {
      return publicApi.publicReadRaidV3(prefix + "/" + suffix);
    }
    catch( NoDataFoundException ex ){
      // Don't need to log it, it's visible from the RequestLoggingFilter
      throw new ResponseStatusException(NOT_FOUND);
    }
  }

  /**
   This is done manually instead of via OpenAPI, because:
   (A) openapi can't generate two mapping differentiated only by the "content"
   key
   (B) it's not really part of the "API", we don't expect/want integrators
   to be using the HTML directly (besides, we render via React, so it would 
   likely be a huge pain for them to use). They should call the API version and
   deal with the JSON.
   */
  @RequestMapping(
    method = RequestMethod.GET,
    value = "/{prefix}/{suffix}",
    produces = {TEXT_HTML_VALUE}
  )
  public ResponseEntity<Void> publicBrowserViewRaid(
    @PathVariable("prefix") String prefix,
    @PathVariable("suffix") String suffix
  ) {
    if( hasValue(prefix) && hasValue(suffix) ){
      return ResponseEntity.status(FOUND).
        header(LOCATION, env.raidoLandingPage+"/"+prefix+"/" + suffix).
        body(null);
    }

    /* Not sure about this yet, might want it to redirect to website? */
    throw new ResponseStatusException(NOT_FOUND);
  }
}
