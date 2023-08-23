package au.org.raid.api.service.raid.validation;

import au.org.raid.idl.raidv2.model.SpatialCoverageBlock;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class SpatialCoverageValidationService {
    private static final String URI_PATTERN =
            "^https://www\\.geonames\\.org/[\\d]+/[\\w]+\\.html";

    private static final String SPATIAL_COVERAGE_SCHEME_URI = "https://www.geonames.org/";

    public List<ValidationFailure> validateSpatialCoverages(final List<SpatialCoverageBlock> spatialCoverages) {
        final var failures = new ArrayList<ValidationFailure>();

        if (spatialCoverages == null) {
            return failures;
        }

        IntStream.range(0, spatialCoverages.size())
                .forEach(i -> {
                    final var spatialCoverage = spatialCoverages.get(i);

                    if (spatialCoverage.getSpatialCoverage() == null) {
                        failures.add(new ValidationFailure()
                                .fieldId(String.format("spatialCoverages[%d].spatialCoverage", i))
                                .errorType("required")
                                .message("This is a required field."));
                    } else if (!spatialCoverage.getSpatialCoverage().matches(URI_PATTERN)) {
                        failures.add(new ValidationFailure()
                                .fieldId(String.format("spatialCoverages[%d].spatialCoverage", i))
                                .errorType("invalid")
                                .message("Uri is invalid."));
                    }
                    if (spatialCoverage.getSpatialCoverageSchemeUri() == null) {
                        failures.add(new ValidationFailure()
                                .fieldId(String.format("spatialCoverages[%d].spatialCoverageSchemeUri", i))
                                .errorType("required")
                                .message("This is a required field."));
                    } else if (!spatialCoverage.getSpatialCoverageSchemeUri().equals(SPATIAL_COVERAGE_SCHEME_URI)) {
                        failures.add(new ValidationFailure()
                                .fieldId(String.format("spatialCoverages[%d].spatialCoverageSchemeUri", i))
                                .errorType("invalid")
                                .message(String.format("Spatial coverage scheme uri should be %s.", SPATIAL_COVERAGE_SCHEME_URI)));
                    }
                });


        return failures;
    }


}
