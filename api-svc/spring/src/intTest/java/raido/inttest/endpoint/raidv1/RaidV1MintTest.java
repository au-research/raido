package raido.inttest.endpoint.raidv1;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import raido.apisvc.util.Log;
import raido.idl.raidv1.model.RaidCreateModel;
import raido.idl.raidv1.model.RaidModel;
import raido.inttest.IntegrationTestCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.RestUtil.urlEncode;

public class RaidV1MintTest extends IntegrationTestCase {
  private static final Log log = to(RaidV1MintTest.class);

  public static String getForbiddenMessage(String path) {
    return """
      403 Forbidden: "{<EOL>"message":"Access Denied",<EOL>"url":"%s",<EOL>"status":"403"<EOL>}"
      """.formatted(path).trim();
  }

  
  @Test void shoudRejectAnonCalltoMint(){
    assertThatThrownBy(()->anonPost("/v1/raid", "{}", Object.class)).
      isInstanceOf(HttpClientErrorException.class).
      /* full text match to ensure there's no info leakage */
        hasMessage(getForbiddenMessage("/v1/raid"));
  }

  @Test
  void mintShouldRejectMissingContentPath() {
    assertThatThrownBy(()->
      post(raidV1TestToken, "/v1/raid", new RaidCreateModel(), RaidModel.class)
    ).isInstanceOf(HttpServerErrorException.class).
      hasMessageContaining("no contentPath");
  }
  
  @Test void mintAndGetHandleShoudSucceed(){
    var create = new RaidCreateModel().
      contentPath("https://example.com");
    var mintResult = post(raidV1TestToken, "/v1/raid", create, RaidModel.class);
    assertThat(mintResult).isNotNull();
    assertThat(mintResult.getHandle()).isNotBlank();
    
    var encodedHandle = urlEncode(mintResult.getHandle());
    log.info("handle:" + mintResult.getHandle());
    log.info("encoded handle:" + encodedHandle);
    var getResult = get(raidV1TestToken, 
      "/v1/handle/"+encodedHandle, RaidModel.class);
    assertThat(mintResult).isNotNull();
    assertThat(getResult.getHandle()).isEqualTo(mintResult.getHandle());
    assertThat(getResult.getOwner()).isEqualTo(authTokenSvc.testOwner);
  }
  
}

