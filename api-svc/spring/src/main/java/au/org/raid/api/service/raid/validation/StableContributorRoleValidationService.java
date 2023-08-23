package au.org.raid.api.service.raid.validation;

import au.org.raid.api.repository.ContributorRoleRepository;
import au.org.raid.api.repository.ContributorRoleSchemeRepository;
import au.org.raid.idl.raidv2.model.ContributorRoleWithSchemeUri;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static au.org.raid.api.endpoint.message.ValidationMessage.*;
import static au.org.raid.api.util.StringUtil.isBlank;

@Component
public class StableContributorRoleValidationService {
    private final ContributorRoleSchemeRepository contributorRoleSchemeRepository;
    private final ContributorRoleRepository contributorRoleRepository;

    public StableContributorRoleValidationService(final ContributorRoleSchemeRepository contributorRoleSchemeRepository, final ContributorRoleRepository contributorRoleRepository) {
        this.contributorRoleSchemeRepository = contributorRoleSchemeRepository;
        this.contributorRoleRepository = contributorRoleRepository;
    }

    public List<ValidationFailure> validate(
            final ContributorRoleWithSchemeUri role, final int contributorIndex, final int roleIndex) {
        final var failures = new ArrayList<ValidationFailure>();

        if (isBlank(role.getId())) {
            failures.add(
                    new ValidationFailure()
                            .fieldId("contributors[%d].roles[%d].id".formatted(contributorIndex, roleIndex))
                            .errorType(NOT_SET_TYPE)
                            .message(FIELD_MUST_BE_SET_MESSAGE));
        }

        if (isBlank(role.getSchemeUri())) {
            failures.add(
                    new ValidationFailure()
                            .fieldId("contributors[%d].roles[%d].schemeUri".formatted(contributorIndex, roleIndex))
                            .errorType(NOT_SET_TYPE)
                            .message(FIELD_MUST_BE_SET_MESSAGE)
            );
        } else {
            final var roleScheme =
                    contributorRoleSchemeRepository.findByUri(role.getSchemeUri());

            if (roleScheme.isEmpty()) {
                failures.add(
                        new ValidationFailure()
                                .fieldId("contributors[%d].roles[%d].schemeUri".formatted(contributorIndex, roleIndex))
                                .errorType(INVALID_VALUE_TYPE)
                                .message(INVALID_SCHEME)
                );
            } else if (!isBlank(role.getId()) &&
                    contributorRoleRepository.findByUriAndSchemeId(role.getId(), roleScheme.get().getId()).isEmpty()) {
                failures.add(
                        new ValidationFailure()
                                .fieldId("contributors[%d].roles[%d].id".formatted(contributorIndex, roleIndex))
                                .errorType(INVALID_VALUE_TYPE)
                                .message(INVALID_ID_FOR_SCHEME)
                );
            }
        }

        return failures;
    }
}