package au.org.raid.api.service.raid.validation;

import au.org.raid.api.repository.ContributorPositionRepository;
import au.org.raid.api.repository.ContributorPositionSchemeRepository;
import au.org.raid.idl.raidv2.model.ContributorPositionWithSchemeUri;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static au.org.raid.api.endpoint.message.ValidationMessage.*;
import static au.org.raid.api.util.StringUtil.isBlank;

@Component
public class StableContributorPositionValidationService {
    private final ContributorPositionSchemeRepository contributorPositionSchemeRepository;
    private final ContributorPositionRepository contributorPositionRepository;

    public StableContributorPositionValidationService(final ContributorPositionSchemeRepository contributorPositionSchemeRepository, final ContributorPositionRepository contributorPositionRepository) {
        this.contributorPositionSchemeRepository = contributorPositionSchemeRepository;
        this.contributorPositionRepository = contributorPositionRepository;
    }

    public List<ValidationFailure> validate(
            final ContributorPositionWithSchemeUri position, final int contributorIndex, final int positionIndex) {
        final var failures = new ArrayList<ValidationFailure>();

        if (position.getStartDate() == null) {
            failures.add(
                    new ValidationFailure()
                            .fieldId("contributors[%d].positions[%d].startDate".formatted(contributorIndex, positionIndex))
                            .errorType(NOT_SET_TYPE)
                            .message(FIELD_MUST_BE_SET_MESSAGE));
        }

        if (isBlank(position.getId())) {
            failures.add(
                    new ValidationFailure()
                            .fieldId("contributors[%d].positions[%d].id".formatted(contributorIndex, positionIndex))
                            .errorType(NOT_SET_TYPE)
                            .message(FIELD_MUST_BE_SET_MESSAGE));
        }

        if (isBlank(position.getSchemeUri())) {
            failures.add(
                    new ValidationFailure()
                            .fieldId("contributors[%d].positions[%d].schemeUri".formatted(contributorIndex, positionIndex))
                            .errorType(NOT_SET_TYPE)
                            .message(FIELD_MUST_BE_SET_MESSAGE)
            );
        } else {
            final var positionScheme =
                    contributorPositionSchemeRepository.findByUri(position.getSchemeUri());

            if (positionScheme.isEmpty()) {
                failures.add(
                        new ValidationFailure()
                                .fieldId("contributors[%d].positions[%d].schemeUri".formatted(contributorIndex, positionIndex))
                                .errorType(INVALID_VALUE_TYPE)
                                .message(INVALID_SCHEME)
                );
            } else if (!isBlank(position.getId()) &&
                    contributorPositionRepository.findByUriAndSchemeId(position.getId(), positionScheme.get().getId()).isEmpty()) {
                failures.add(
                        new ValidationFailure()
                                .fieldId("contributors[%d].positions[%d].id".formatted(contributorIndex, positionIndex))
                                .errorType(INVALID_VALUE_TYPE)
                                .message(INVALID_ID_FOR_SCHEME)
                );
            }
        }

        return failures;
    }
}