package raido.inttest.endpoint.raidv1;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import raido.apisvc.util.Log;
import raido.inttest.IntegrationTestCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static raido.apisvc.util.Log.to;

public class RaidPingTest extends IntegrationTestCase {
  private static final Log log = to(RaidPingTest.class);
  
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
      get(testToken, "/v1/raid/ping", Result.class).status
    ).isEqualTo("UP");
  }

  @Test
  public void shouldCallApidsMint() {
    assertThat(
      post(testToken, "/v1/raid/apidstest", "", Result.class).status
    ).isEqualTo("UP");
  }

  // IMPROVE:STO use proper API model, when it happens  
  static final class Result {
    public String status;
  }
  
}

