package au.org.raid.api.validator;

import au.org.raid.api.repository.OrganisationRoleRepository;
import au.org.raid.api.repository.OrganisationRoleSchemeRepository;
import au.org.raid.idl.raidv2.model.OrganisationRoleWithSchemeUri;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static au.org.raid.api.endpoint.message.ValidationMessage.*;
import static au.org.raid.api.util.StringUtil.isBlank;

@Component
public class OrganisationRoleValidator {
    private final OrganisationRoleSchemeRepository organisationRoleSchemeRepository;
    private final OrganisationRoleRepository organisationRoleRepository;

    public OrganisationRoleValidator(final OrganisationRoleSchemeRepository organisationRoleSchemeRepository, final OrganisationRoleRepository organisationRoleRepository) {
        this.organisationRoleSchemeRepository = organisationRoleSchemeRepository;
        this.organisationRoleRepository = organisationRoleRepository;
    }

    public List<ValidationFailure> validate(
            final OrganisationRoleWithSchemeUri role, final int organisationIndex, final int roleIndex) {
        final var failures = new ArrayList<ValidationFailure>();

        if (isBlank(role.getId())) {
            failures.add(
                    new ValidationFailure()
                            .fieldId("organisations[%d].roles[%d].id".formatted(organisationIndex, roleIndex))
                            .errorType(NOT_SET_TYPE)
                            .message(FIELD_MUST_BE_SET_MESSAGE));
        }

        if (isBlank(role.getSchemeUri())) {
            failures.add(
                    new ValidationFailure()
                            .fieldId("organisations[%d].roles[%d].schemeUri".formatted(organisationIndex, roleIndex))
                            .errorType(NOT_SET_TYPE)
                            .message(FIELD_MUST_BE_SET_MESSAGE)
            );
        } else {
            final var roleScheme =
                    organisationRoleSchemeRepository.findByUri(role.getSchemeUri());

            if (roleScheme.isEmpty()) {
                failures.add(
                        new ValidationFailure()
                                .fieldId("organisations[%d].roles[%d].schemeUri".formatted(organisationIndex, roleIndex))
                                .errorType(INVALID_VALUE_TYPE)
                                .message(INVALID_SCHEME)
                );
            } else if (!isBlank(role.getId()) &&
                    organisationRoleRepository.findByUriAndSchemeId(role.getId(), roleScheme.get().getId()).isEmpty()) {
                failures.add(
                        new ValidationFailure()
                                .fieldId("organisations[%d].roles[%d].id".formatted(organisationIndex, roleIndex))
                                .errorType(INVALID_VALUE_TYPE)
                                .message(INVALID_ID_FOR_SCHEME)
                );
            }
        }

        return failures;
    }
}