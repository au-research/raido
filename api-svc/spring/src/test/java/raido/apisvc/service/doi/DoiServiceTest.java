package raido.apisvc.service.doi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoiServiceTest {
  public static final String TEST_DOI = "https://doi.org/10.a/test-doi";
  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private DoiService doiService;

  @Test
  void returnsListOfMessagesIfRequestFails() {
    doThrow(new HttpClientErrorException(HttpStatusCode.valueOf(404))).
      when(restTemplate).exchange(any(RequestEntity.class), eq(Void.class));

    final List<String> messages = doiService.validateDoiExists(TEST_DOI);

    assertThat(messages.size(), is(1));
    assertThat(messages.get(0), is("The DOI does not exist."));
  }

  @Test
  void returnsEmptyListOfMessagesIfRequestSucceeds() {

    when(restTemplate.exchange(any(RequestEntity.class), eq(Void.class))).thenReturn(ResponseEntity.ok().build());

    final List<String> messages = doiService.validateDoiExists(TEST_DOI);

    assertThat(messages, is(empty()));
  }
}