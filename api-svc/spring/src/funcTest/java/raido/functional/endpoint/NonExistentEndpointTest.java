package raido.functional.endpoint;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import raido.functional.FunctionalTestCase;
import raido.util.Log;
import raido.util.test.BddUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static raido.util.Log.to;

public class NonExistentEndpointTest extends FunctionalTestCase {
  private static final Log log = to(NonExistentEndpointTest.class);

  @Test
  public void nonExistentEndpointShould404() {
    BddUtil.GIVEN("that an existing endpoint is callable");
    assertThat(
      anonGet("/public/status", Result.class).status
    ).isEqualTo("UP");

    BddUtil.EXPECT("that an non-existing endpoint returns 404");
    assertThatThrownBy(()->{
      anonGet("/public/does-not-exist", String.class);
    }).
      isInstanceOf(HttpClientErrorException.class).
      /* should do a full text match to assert no info leakage, 
      messing with regex to deal with the timestamp was taking too long */
      hasMessageContaining("404 Not Found");
  }

  // IMPROVE:STO use proper API model, when it happens  
  static final class Result {
    public String status;
  }
}

