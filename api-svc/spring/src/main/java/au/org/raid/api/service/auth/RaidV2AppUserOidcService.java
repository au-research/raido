package au.org.raid.api.service.auth;

import au.org.raid.api.util.Log;
import au.org.raid.db.jooq.api_svc.enums.IdProvider;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import static au.org.raid.api.spring.security.IdProviderException.idpException;
import static au.org.raid.api.util.Log.to;

/**
 Handles signing and verifying JWTs for signing in (does not handle api-keys).
 */
@Component
public class RaidV2AppUserOidcService {
  private static final Log log = to(RaidV2AppUserOidcService.class);

  private AafOidc aaf;
  private GoogleOidc google;
  private OrcidOidc orcid;
  
  public RaidV2AppUserOidcService(
    AafOidc aaf,
    GoogleOidc google,
    OrcidOidc orcid
  ) {
    this.aaf = aaf;
    this.google = google;
    this.orcid = orcid;
  }

  /**
   Returns the verified `id_token` from the IDP by calling the OIDC /token
   endpoint, which endpoint is called is based on the clientId.
   */
  public DecodedJWT exchangeOAuthCodeForIdToken(
    String clientId,
    String idpResponseCode
  ){
    var idProvider = mapIdProvider(clientId);
    DecodedJWT idProviderJwt;
    switch( idProvider ){
      case GOOGLE -> idProviderJwt =
        google.exchangeOAuthCodeForVerifiedIdToken(idpResponseCode);
      case AAF -> idProviderJwt =
        aaf.exchangeOAuthCodeForVerifiedIdToken(idpResponseCode);
      case ORCID -> idProviderJwt =
        orcid.exchangeOAuthCodeForVerifiedIdToken(idpResponseCode);
      default -> throw idpException("unknown clientId %s", clientId);
    }

    return idProviderJwt;
  }

  public IdProvider mapIdProvider(String clientId){
    if( aaf.canHandle(clientId) ){
      return IdProvider.AAF;
    }
    else if( google.canHandle(clientId) ){
      return IdProvider.GOOGLE;
    }
    else if( orcid.canHandle(clientId) ){
      return IdProvider.ORCID;
    }
    else {
      // improve should be a specific authn error?
      // and should it expose the error?
      throw idpException("unknown clientId %s", clientId);
    }
  }
  

}
