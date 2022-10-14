package raido.apisvc.service.apids;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import raido.apisvc.service.apids.model.ApidsMintResponse;
import raido.apisvc.spring.config.ApiConfig;
import raido.apisvc.spring.config.environment.ApidsProps;

import java.net.URI;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static raido.apisvc.service.apids.ApidsService.formatMintParams;

public class ApidsTest {

  public static final String UNIT_TEST_CONTENT_PATH = 
    "https://unitTestContentPath";

  @Test
  public void shouldParseMintResponse() throws Exception {
    var props = new ApidsProps();
    props.appId = "";
    props.secret = "";
    props.serviceUrl = "testserver";

    RestTemplate restTemplate = ApiConfig.restTemplate();

    MockRestServiceServer mockServer = 
      MockRestServiceServer.createServer(restTemplate);
    mockServer.expect( requestTo(
      new URI("/testserver?" + formatMintParams(UNIT_TEST_CONTENT_PATH)) 
    )).
      andExpect( method(POST) ).
      andRespond( 
        withStatus(OK).
        contentType(APPLICATION_XML).
        body(ApidsMintResponse.mintSuccessExample) );

    var svc = new ApidsService(props, restTemplate);
    var response = svc.mintApidsHandle(UNIT_TEST_CONTENT_PATH);

    // hardcoded in the exampleResponse
    assertThat(response.identifier.handle).isEqualTo("10378.1/1687706");
  }
}
