package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.Test;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import raido.apisvc.spring.config.environment.MetadataProps;
import raido.idl.raidv2.model.RelatedRaidBlock;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class RelatedRaidValidationServiceTest {
  private final RestTemplate restTemplate = mock(RestTemplate.class);

  private final MetadataProps metadataProps = new MetadataProps();

  private  final RelatedRaidValidationService validationService =
    new RelatedRaidValidationService(restTemplate, metadataProps);

  @Test
  void addsValidationFailureIfUrlIsIncorrect() {
    metadataProps.handleUrlPrefix = "handle-url-prefix";
    final var uri = "example.com";

    final var relatedRaid = new RelatedRaidBlock().relatedRaid(uri);

    final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

    final var validationFailure = validationFailures.get(0);
    assertThat(validationFailures.size(), is(1));
    assertThat(validationFailure.getFieldId(), is("relatedRaids[0].relatedRaid"));
    assertThat(validationFailure.getErrorType(), is("invalid"));
    assertThat(validationFailure.getMessage(), is("Related Raid is invalid"));

    verifyNoInteractions(restTemplate);
  }

  @Test
  void addsValidationFailureIfRaidRequestIsNotSuccessful() {
    metadataProps.handleUrlPrefix = "handle-url-prefix";
    final var uri = "handle-url-prefix";

    doThrow(HttpClientErrorException.class).when(restTemplate).exchange(any(RequestEntity.class), eq(Void.class));

    final var relatedRaid = new RelatedRaidBlock().relatedRaid(uri);

    final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

    final var validationFailure = validationFailures.get(0);
    assertThat(validationFailures.size(), is(1));
    assertThat(validationFailure.getFieldId(), is("relatedRaids[0].relatedRaid"));
    assertThat(validationFailure.getErrorType(), is("invalid"));
    assertThat(validationFailure.getMessage(), is("Related Raid was not found."));

    verify(restTemplate, times(1)).exchange(any(RequestEntity.class), eq(Void.class));
  }

  @Test
  void noValidationFailuresWhenRaidIsFound() {
    metadataProps.handleUrlPrefix = "handle-url-prefix";
    final var uri = "handle-url-prefix";

    when(restTemplate.exchange(any(RequestEntity.class), eq(Void.class))).thenReturn(ResponseEntity.ok().build());

    final var relatedRaid = new RelatedRaidBlock().relatedRaid(uri);

    final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

    assertThat(validationFailures, is(empty()));
    verify(restTemplate, times(1)).exchange(any(RequestEntity.class), eq(Void.class));
  }


  @Test
  void noValidationFailuresRelatedRaidBlockIsNull() {
    when(restTemplate.exchange(any(RequestEntity.class), eq(Void.class))).thenReturn(ResponseEntity.ok().build());

    final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(null);

    assertThat(validationFailures, is(empty()));
    verifyNoInteractions(restTemplate);
  }

  @Test
  void noValidationFailuresRelatedRaidsIsEmpty() {
    when(restTemplate.exchange(any(RequestEntity.class), eq(Void.class))).thenReturn(ResponseEntity.ok().build());

    final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(Collections.emptyList());

    assertThat(validationFailures, is(empty()));
    verifyNoInteractions(restTemplate);
  }
}