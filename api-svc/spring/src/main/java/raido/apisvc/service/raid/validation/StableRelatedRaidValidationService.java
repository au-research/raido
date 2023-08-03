package raido.apisvc.service.raid.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import raido.apisvc.repository.RelatedRaidTypeRepository;
import raido.apisvc.service.doi.DoiService;
import raido.idl.raidv2.model.RelatedRaid;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static raido.apisvc.endpoint.message.ValidationMessage.*;
import static raido.apisvc.service.doi.DoiService.DOI_REGEX;
import static raido.apisvc.util.StringUtil.isBlank;

@Service
@RequiredArgsConstructor
public class StableRelatedRaidValidationService {

  private final StableRelatedRaidTypeValidationService typeValidationService;

  public List<ValidationFailure> validate(final List<RelatedRaid> relatedRaids) {
    final var failures = new ArrayList<ValidationFailure>();

    if (relatedRaids == null) {
      return failures;
    }

    IntStream.range(0, relatedRaids.size())
        .forEach(index -> {
          final var relatedRaid = relatedRaids.get(index);

          if (isBlank(relatedRaid.getId())) {
            failures.add(new ValidationFailure()
                .fieldId(String.format("relatedRaids[%d].id", index))
                .errorType(NOT_SET_TYPE)
                .message(FIELD_MUST_BE_SET_MESSAGE));
          }
          // TODO: Validate Raid exists

          failures.addAll(typeValidationService.validate(relatedRaid.getType(), index));
        });

    return failures;
  }
}