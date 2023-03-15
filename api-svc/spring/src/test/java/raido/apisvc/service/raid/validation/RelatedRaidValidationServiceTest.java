package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import raido.apisvc.spring.config.environment.MetadataProps;
import raido.idl.raidv2.model.RelatedRaidBlock;
import raido.idl.raidv2.model.ValidationFailure;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class RelatedRaidValidationServiceTest {

  private static final String RELATIONSHIP_TYPE_URL_PREFIX =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/relationship-type/";

  private final RestTemplate restTemplate = mock(RestTemplate.class);

  private final MetadataProps metadataProps = new MetadataProps();

  private  final RelatedRaidValidationService validationService =
    new RelatedRaidValidationService(restTemplate, metadataProps);

  @Test
  void addsValidationFailureIfRelatedRaidUrlIsIncorrect() {
    metadataProps.handleUrlPrefix = "handle-url-prefix";
    final var uri = "example.com";

    final var relatedRaidRequestEntity = mock(RequestEntity.class);

    final var relatedRaidRequestHeadersBuilder = mock(RequestEntity.HeadersBuilder.class);
    when(relatedRaidRequestHeadersBuilder.build()).thenReturn(relatedRaidRequestEntity);

    try (MockedStatic<RequestEntity> requestEntityMock = Mockito.mockStatic(RequestEntity.class)) {
      requestEntityMock.when(() -> RequestEntity.head(RELATIONSHIP_TYPE_URL_PREFIX)).thenReturn(relatedRaidRequestHeadersBuilder);

      when(restTemplate.exchange(relatedRaidRequestEntity, Void.class)).thenReturn(ResponseEntity.ok().build());
      final var relatedRaid = new RelatedRaidBlock()
        .relatedRaid(uri)
        .relatedRaidType(RELATIONSHIP_TYPE_URL_PREFIX);

      final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

      final var validationFailure = validationFailures.get(0);
      assertThat(validationFailures.size(), is(1));
      assertThat(validationFailure.getFieldId(), is("relatedRaids[0].relatedRaid"));
      assertThat(validationFailure.getErrorType(), is("invalid"));
      assertThat(validationFailure.getMessage(), is("RelatedRaid is invalid."));

      verify(restTemplate).exchange(relatedRaidRequestEntity, Void.class);
      verifyNoMoreInteractions(restTemplate);
    }
  }

  @Test
  void addsValidationFailureIfRelatedRaidRequestIsNotSuccessful() {
    metadataProps.handleUrlPrefix = "handle-url-prefix";
    final var raidUri = "handle-url-prefix";
    final var raidRequestEntity = mock(RequestEntity.class);
    final var relatedRaidRequestEntity = mock(RequestEntity.class);

    final var raidRequestHeadersBuilder = mock(RequestEntity.HeadersBuilder.class);
    when(raidRequestHeadersBuilder.build()).thenReturn(raidRequestEntity);

    final var relatedRaidRequestHeadersBuilder = mock(RequestEntity.HeadersBuilder.class);
    when(relatedRaidRequestHeadersBuilder.build()).thenReturn(relatedRaidRequestEntity);

    try (MockedStatic<RequestEntity> requestEntityMock = Mockito.mockStatic(RequestEntity.class)) {
      requestEntityMock.when(() -> RequestEntity.head(raidUri)).thenReturn(raidRequestHeadersBuilder);
      requestEntityMock.when(() -> RequestEntity.head(RELATIONSHIP_TYPE_URL_PREFIX)).thenReturn(relatedRaidRequestHeadersBuilder);

      doThrow(HttpClientErrorException.class).when(restTemplate).exchange(raidRequestEntity, Void.class);
      when(restTemplate.exchange(relatedRaidRequestEntity, Void.class)).thenReturn(ResponseEntity.ok().build());

      final var relatedRaid = new RelatedRaidBlock()
        .relatedRaid(raidUri)
        .relatedRaidType(RELATIONSHIP_TYPE_URL_PREFIX);

      final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

      final var validationFailure = validationFailures.get(0);
      assertThat(validationFailures.size(), is(1));
      assertThat(validationFailure.getFieldId(), is("relatedRaids[0].relatedRaid"));
      assertThat(validationFailure.getErrorType(), is("invalid"));
      assertThat(validationFailure.getMessage(), is("Related Raid was not found."));

      verify(restTemplate, times(1)).exchange(raidRequestEntity, Void.class);
      verify(restTemplate, times(1)).exchange(relatedRaidRequestEntity, Void.class);
    }
  }

  @Test
  void noValidationFailuresWhenRelatedRaidIsFound() {
    metadataProps.handleUrlPrefix = "handle-url-prefix";
    final var raidUri = "handle-url-prefix";
    final var raidRequestEntity = mock(RequestEntity.class);
    final var relatedRaidRequestEntity = mock(RequestEntity.class);

    final var raidRequestHeadersBuilder = mock(RequestEntity.HeadersBuilder.class);
    when(raidRequestHeadersBuilder.build()).thenReturn(raidRequestEntity);

    final var relatedRaidRequestHeadersBuilder = mock(RequestEntity.HeadersBuilder.class);
    when(relatedRaidRequestHeadersBuilder.build()).thenReturn(relatedRaidRequestEntity);

    try (MockedStatic<RequestEntity> requestEntityMock = Mockito.mockStatic(RequestEntity.class)) {
      requestEntityMock.when(() -> RequestEntity.head(raidUri)).thenReturn(raidRequestHeadersBuilder);
      requestEntityMock.when(() -> RequestEntity.head(RELATIONSHIP_TYPE_URL_PREFIX)).thenReturn(relatedRaidRequestHeadersBuilder);

      when(restTemplate.exchange(raidRequestEntity, Void.class)).thenReturn(ResponseEntity.ok().build());
      when(restTemplate.exchange(relatedRaidRequestEntity, Void.class)).thenReturn(ResponseEntity.ok().build());

      final var relatedRaid = new RelatedRaidBlock()
        .relatedRaid(raidUri)
        .relatedRaidType(RELATIONSHIP_TYPE_URL_PREFIX);

      final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

      assertThat(validationFailures, is(empty()));
      verify(restTemplate, times(1)).exchange(raidRequestEntity, Void.class);
      verify(restTemplate, times(1)).exchange(relatedRaidRequestEntity, Void.class);
    }
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

  @Test
  void addsValidationFailuresWhenRelatedRaidTypeHasIncorrectUrl() throws URISyntaxException {
    metadataProps.handleUrlPrefix = "handle-url-prefix";
    final var raidUri = "handle-url-prefix";
    final var relatedRaidUri = "related-raid-uri";
    final var raidRequestEntity = mock(RequestEntity.class);

    final var raidRequestHeadersBuilder = mock(RequestEntity.HeadersBuilder.class);
    when(raidRequestHeadersBuilder.build()).thenReturn(raidRequestEntity);

    try (MockedStatic<RequestEntity> requestEntityMock = Mockito.mockStatic(RequestEntity.class)) {
      requestEntityMock.when(() -> RequestEntity.head(raidUri)).thenReturn(raidRequestHeadersBuilder);

      when(restTemplate.exchange(raidRequestEntity, Void.class)).thenReturn(ResponseEntity.ok().build());

      final var relatedRaid = new RelatedRaidBlock()
        .relatedRaid(raidUri)
        .relatedRaidType(relatedRaidUri);

      final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

      final var validationFailure = validationFailures.get(0);
      assertThat(validationFailures.size(), is(1));
      assertThat(validationFailure.getFieldId(), is("relatedRaids[0].relatedRaidType"));
      assertThat(validationFailure.getErrorType(), is("invalid"));
      assertThat(validationFailure.getMessage(), is("RelatedRaidType is invalid."));
      verify(restTemplate, times(1)).exchange(raidRequestEntity, Void.class);
    }
  }

  @Test
  void addsValidationFailuresWhenRelatedRaidNotFound() throws URISyntaxException {
    metadataProps.handleUrlPrefix = "handle-url-prefix";
    final var raidUri = "handle-url-prefix";
    final var raidRequestEntity = mock(RequestEntity.class);
    final var relatedRaidRequestEntity = mock(RequestEntity.class);

    final var raidRequestHeadersBuilder = mock(RequestEntity.HeadersBuilder.class);
    when(raidRequestHeadersBuilder.build()).thenReturn(raidRequestEntity);

    final var relatedRaidRequestHeadersBuilder = mock(RequestEntity.HeadersBuilder.class);
    when(relatedRaidRequestHeadersBuilder.build()).thenReturn(relatedRaidRequestEntity);

    try (MockedStatic<RequestEntity> requestEntityMock = Mockito.mockStatic(RequestEntity.class)) {
      requestEntityMock.when(() -> RequestEntity.head(raidUri)).thenReturn(raidRequestHeadersBuilder);
      requestEntityMock.when(() -> RequestEntity.head(RELATIONSHIP_TYPE_URL_PREFIX)).thenReturn(relatedRaidRequestHeadersBuilder);

      when(restTemplate.exchange(raidRequestEntity, Void.class)).thenReturn(ResponseEntity.ok().build());

      doThrow(HttpClientErrorException.class).when(restTemplate).exchange(relatedRaidRequestEntity, Void.class);

      final var relatedRaid = new RelatedRaidBlock()
        .relatedRaid(raidUri)
        .relatedRaidType(RELATIONSHIP_TYPE_URL_PREFIX);

      final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

      final var validationFailure = validationFailures.get(0);
      assertThat(validationFailures.size(), is(1));
      assertThat(validationFailure.getFieldId(), is("relatedRaids[0].relatedRaidType"));
      assertThat(validationFailure.getErrorType(), is("invalid"));
      assertThat(validationFailure.getMessage(), is("Related Raid Type was not found."));
      verify(restTemplate, times(1)).exchange(raidRequestEntity, Void.class);
      verify(restTemplate, times(1)).exchange(relatedRaidRequestEntity, Void.class);
    }
  }

  @Test
  void addsValidationFailuresWhenRelatedRaidAndTypeNotFound() throws URISyntaxException {
    metadataProps.handleUrlPrefix = "handle-url-prefix";
    final var raidUri = "handle-url-prefix";
    final var raidRequestEntity = mock(RequestEntity.class);
    final var relatedRaidRequestEntity = mock(RequestEntity.class);

    final var raidRequestHeadersBuilder = mock(RequestEntity.HeadersBuilder.class);
    when(raidRequestHeadersBuilder.build()).thenReturn(raidRequestEntity);

    final var relatedRaidRequestHeadersBuilder = mock(RequestEntity.HeadersBuilder.class);
    when(relatedRaidRequestHeadersBuilder.build()).thenReturn(relatedRaidRequestEntity);

    try (MockedStatic<RequestEntity> requestEntityMock = Mockito.mockStatic(RequestEntity.class)) {
      requestEntityMock.when(() -> RequestEntity.head(raidUri)).thenReturn(raidRequestHeadersBuilder);
      requestEntityMock.when(() -> RequestEntity.head(RELATIONSHIP_TYPE_URL_PREFIX)).thenReturn(relatedRaidRequestHeadersBuilder);

      doThrow(HttpClientErrorException.class).when(restTemplate).exchange(raidRequestEntity, Void.class);
      doThrow(HttpClientErrorException.class).when(restTemplate).exchange(relatedRaidRequestEntity, Void.class);

      final var relatedRaid = new RelatedRaidBlock()
        .relatedRaid(raidUri)
        .relatedRaidType(RELATIONSHIP_TYPE_URL_PREFIX);

      final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

      final var validationFailure = validationFailures.get(0);
      assertThat(validationFailures.size(), is(2));


      assertThat(validationFailures.get(0).getFieldId(), is("relatedRaids[0].relatedRaid"));
      assertThat(validationFailures.get(0).getErrorType(), is("invalid"));
      assertThat(validationFailures.get(0).getMessage(), is("Related Raid was not found."));

      assertThat(validationFailures.get(1).getFieldId(), is("relatedRaids[0].relatedRaidType"));
      assertThat(validationFailures.get(1).getErrorType(), is("invalid"));
      assertThat(validationFailures.get(1).getMessage(), is("Related Raid Type was not found."));





      verify(restTemplate, times(1)).exchange(raidRequestEntity, Void.class);
      verify(restTemplate, times(1)).exchange(relatedRaidRequestEntity, Void.class);
    }
  }
}