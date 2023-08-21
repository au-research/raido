package raido.apisvc.service.raid.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.SpatialCoverage;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static raido.apisvc.endpoint.message.ValidationMessage.*;
import static raido.apisvc.util.StringUtil.isBlank;

@Component
@RequiredArgsConstructor
public class StableSpatialCoverageValidationService {
    private final LanguageValidationService languageValidationService;
  private static final String URI_PATTERN =
    "^https://www\\.geonames\\.org/[\\d]+/[\\w]+\\.html";

  private static final String SPATIAL_COVERAGE_SCHEME_URI = "https://www.geonames.org/";

  public List<ValidationFailure> validate(final List<SpatialCoverage> spatialCoverages) {
    final var failures = new ArrayList<ValidationFailure>();

    if (spatialCoverages == null) {
      return failures;
    }

    IntStream.range(0, spatialCoverages.size())
      .forEach(i -> {
        final var spatialCoverage = spatialCoverages.get(i);

        if (isBlank(spatialCoverage.getId())) {
          failures.add(new ValidationFailure()
            .fieldId(String.format("spatialCoverages[%d].id", i))
            .errorType(NOT_SET_TYPE)
            .message(FIELD_MUST_BE_SET_MESSAGE));
        } else if (!spatialCoverage.getId().matches(URI_PATTERN)) {
          failures.add(new ValidationFailure()
            .fieldId(String.format("spatialCoverages[%d].id", i))
            .errorType(INVALID_VALUE_TYPE)
            .message(INVALID_VALUE_MESSAGE));
        }
        if (isBlank(spatialCoverage.getSchemeUri())) {
          failures.add(new ValidationFailure()
            .fieldId(String.format("spatialCoverages[%d].schemeUri", i))
            .errorType(NOT_SET_TYPE)
            .message(FIELD_MUST_BE_SET_MESSAGE));
        }
        else if (!spatialCoverage.getSchemeUri().equals(SPATIAL_COVERAGE_SCHEME_URI)) {
          failures.add(new ValidationFailure()
            .fieldId(String.format("spatialCoverages[%d].schemeUri", i))
            .errorType(INVALID_VALUE_TYPE)
            .message(String.format("Spatial coverage scheme uri should be %s", SPATIAL_COVERAGE_SCHEME_URI)));
        }
          failures.addAll(
                  languageValidationService.validate(spatialCoverage.getLanguage(), "spatialCoverages[%d]".formatted(i))
          );
      });

    return failures;
  }
}
