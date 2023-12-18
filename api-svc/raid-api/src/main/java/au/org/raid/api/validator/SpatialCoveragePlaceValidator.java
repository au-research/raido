package au.org.raid.api.validator;

import au.org.raid.idl.raidv2.model.SpatialCoverage;
import au.org.raid.idl.raidv2.model.SpatialCoveragePlace;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static au.org.raid.api.endpoint.message.ValidationMessage.NOT_SET_MESSAGE;
import static au.org.raid.api.endpoint.message.ValidationMessage.NOT_SET_TYPE;
import static au.org.raid.api.util.StringUtil.isBlank;

@Component
@RequiredArgsConstructor
public class SpatialCoveragePlaceValidator {
    private final LanguageValidator languageValidator;

    public List<ValidationFailure> validate(final List<SpatialCoveragePlace> places, final int spatialCoverageIndex) {
        if (places == null || places.isEmpty()) {
            return Collections.emptyList();
        }

        final var failures = new ArrayList<ValidationFailure>();

        IntStream.range(0, places.size()).forEach(i -> {
            final var place = places.get(i);

            if (isBlank(place.getText())) {
                failures.add(new ValidationFailure()
                        .fieldId("spatialCoverage[%d].place[%d]".formatted(spatialCoverageIndex, i))
                        .errorType(NOT_SET_TYPE)
                        .message(NOT_SET_MESSAGE)
                );
            }

            failures.addAll(languageValidator.validate(place.getLanguage(),
                    "spatialCoverage[%d].place[%d]".formatted(spatialCoverageIndex, i)));
        });

        return failures;
    }


}
