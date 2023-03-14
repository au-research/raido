package raido.apisvc.service.raid.validation;

import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import raido.apisvc.spring.config.environment.MetadataProps;
import raido.apisvc.util.Log;
import raido.idl.raidv2.model.RelatedRaidBlock;
import raido.idl.raidv2.model.ValidationFailure;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static raido.apisvc.util.Log.to;

@Component
public class RelatedRaidValidationService {
  private static final Log log = to(RelatedRaidValidationService.class);

  private final RestTemplate restTemplate;
  private final MetadataProps metadataProps;

  public RelatedRaidValidationService(final RestTemplate restTemplate, final MetadataProps metadataProps) {
    this.restTemplate = restTemplate;
    this.metadataProps = metadataProps;
  }

  public List<ValidationFailure> validateRelatedRaids(final List<RelatedRaidBlock> relatedRaids) {
    final var failures = new ArrayList<ValidationFailure>();

    if (relatedRaids == null) {
      return failures;
    }

    for (int i = 0; i < relatedRaids.size(); i++) {
      if (!relatedRaids.get(i).getRelatedRaid().startsWith(metadataProps.handleUrlPrefix)) {
        failures.add(new ValidationFailure()
          .errorType("invalid")
          .fieldId(String.format("relatedRaids[%d].relatedRaid", i))
          .message("Related Raid is invalid")
        );
      }
      else {
        RequestEntity<Void> requestEntity =
          RequestEntity.get(URI.create(relatedRaids.get(i).getRelatedRaid())).build();

        try {
          restTemplate.exchange(requestEntity, Void.class);
        } catch (HttpClientErrorException e) {
          log.warnEx("Raid not found", e);

          failures.add(new ValidationFailure()
            .errorType("invalid")
            .fieldId(String.format("relatedRaids[%d].relatedRaid", i))
            .message("Related Raid was not found.")
          );
        }
      }
    }

    return failures;
  }
}
