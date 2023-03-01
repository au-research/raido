package raido.apisvc.endpoint.raidv2;

import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import raido.apisvc.util.Log;
import raido.idl.raidv2.api.PublicStableApi;
import raido.idl.raidv2.model.PublicReadRaidResponseV3;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.hasValue;

@Scope(proxyMode = TARGET_CLASS)
@RestController
@Transactional
public class PublicStable implements PublicStableApi {
  private static final Log log = to(PublicStable.class);
  
  private PublicExperimental experimentalApi;

  public PublicStable(PublicExperimental experimentalApi) {
    this.experimentalApi = experimentalApi;
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
      return experimentalApi.publicReadRaidV3(prefix + "/" + suffix);
    }
    catch( NoDataFoundException ex ){
      // Don't need to log it, it's visible from the RequestLoggingFilter
      throw new ResponseStatusException(NOT_FOUND);
    }
  }

}
