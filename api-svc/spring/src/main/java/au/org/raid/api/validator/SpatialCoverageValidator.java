package au.org.raid.api.validator;

import au.org.raid.idl.raidv2.model.SpatialCoverage;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static au.org.raid.api.endpoint.message.ValidationMessage.*;
import static au.org.raid.api.util.StringUtil.isBlank;

@Component
@RequiredArgsConstructor
public class SpatialCoverageValidator {
    private static final String SPATIAL_COVERAGE_SCHEME_URI = "https://www.geonames.org/";
    private final LanguageValidator languageValidator;
    private final GeoNamesUriValidator geoNamesUriValidator;

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
                                .message(NOT_SET_MESSAGE));
                    }
                    if (isBlank(spatialCoverage.getSchemaUri())) {
                        failures.add(new ValidationFailure()
                                .fieldId(String.format("spatialCoverages[%d].schemaUri", i))
                                .errorType(NOT_SET_TYPE)
                                .message(NOT_SET_MESSAGE));
                    } else if (!spatialCoverage.getSchemaUri().equals(SPATIAL_COVERAGE_SCHEME_URI)) {
                        failures.add(new ValidationFailure()
                                .fieldId(String.format("spatialCoverages[%d].schemaUri", i))
                                .errorType(INVALID_VALUE_TYPE)
                                .message(String.format("Spatial coverage scheme uri should be %s", SPATIAL_COVERAGE_SCHEME_URI)));
                    }
                    failures.addAll(
                            languageValidator.validate(spatialCoverage.getLanguage(), "spatialCoverages[%d]".formatted(i))
                    );
                    failures.addAll(geoNamesUriValidator.validate(
                            spatialCoverage.getId(), String.format("spatialCoverages[%d].id", i)
                    ));
                });

        return failures;
    }
}
