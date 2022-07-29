package raido.functional.endpoint.raidv1;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import raido.functional.FunctionalTestCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RaidPingTest extends FunctionalTestCase {
  
  public static final String FORBIDDEN_MESSAGE = """
    403 Forbidden: "{<EOL>"message":"Access Denied",<EOL>"url":"/v1/raid/ping",<EOL>"status":"403"<EOL>}"
    """.trim();

  @Test
  public void shouldNotAllowAnonymousCall() {
    assertThatThrownBy(()->anonGet("/v1/raid/ping", Result.class)).
      isInstanceOf(HttpClientErrorException.class).
      /* full text match to ensure there's no info leakage */
      hasMessage(FORBIDDEN_MESSAGE);
  }

  @Test
  public void shouldAllowCallWithToken() {
    assertThat(
      get(props.raidoArdcLiveToken, "/v1/raid/ping", Result.class).status
    ).isEqualTo("UP");
  }

  @Test
  public void mint() {
    assertThat(
      post(props.raidoArdcLiveToken, "/v1/raid/minttest", "", Result.class).status
    ).isEqualTo("UP");
  }

  // IMPROVE:STO use proper API model, when it happens  
  static final class Result {
    public String status;
  }
  
}

