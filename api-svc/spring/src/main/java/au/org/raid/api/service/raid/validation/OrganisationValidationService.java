package au.org.raid.api.service.raid.validation;

import au.org.raid.api.service.ror.RorService;
import au.org.raid.api.util.Log;
import au.org.raid.idl.raidv2.model.OrganisationBlock;
import au.org.raid.idl.raidv2.model.OrganisationRole;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static au.org.raid.api.endpoint.message.ValidationMessage.*;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.ObjectUtil.indexed;
import static au.org.raid.api.util.StringUtil.isBlank;
import static au.org.raid.idl.raidv2.model.OrganisationIdentifierSchemeType.HTTPS_ROR_ORG_;

@Component
public class OrganisationValidationService {

    private static final Log log = to(OrganisationValidationService.class);
    private static final int ROR_LENGTH = 25;

    private final RorService rorService;

    public OrganisationValidationService(
            RorService rorService
    ) {
        this.rorService = rorService;
    }

    public static ValidationFailure organisationInvalidRorFormat(
            int index, String message
    ) {
        return new ValidationFailure().
                fieldId("organisations[%s].id".formatted(index)).
                errorType(INVALID_VALUE_TYPE).
                message(message);
    }

    public List<ValidationFailure> validateOrganisations(
            List<OrganisationBlock> organisations
    ) {

    /* organisations has been confirmed as optional in the metadata schema,
    rationale: an ORCID is quick to create (minutes), RORs can take months. */
        if (organisations == null) {
            return Collections.emptyList();
        }

        var failures = new ArrayList<ValidationFailure>();

        organisations.stream().
                collect(indexed()).
                forEach((i, organisation) -> {
                    Supplier<String> fieldPrefix = () -> "organisations[%s]".formatted(i);

                    failures.addAll(validateIdFields(i, organisation));
                    failures.addAll(
                            validateRoleFields(fieldPrefix, organisation.getRoles()));

                });

        return failures;
    }

    public List<ValidationFailure> validateIdFields(
            int i, OrganisationBlock organisation
    ) {
        var failures = new ArrayList<ValidationFailure>();
        if (isBlank(organisation.getId())) {
            failures.add(organisationIdNotSet(i));
        }
        if (organisation.getIdentifierSchemeUri() == null) {
            failures.add(organisationIdSchemeNotSet(i));
        } else {
            if (organisation.getIdentifierSchemeUri() == HTTPS_ROR_ORG_) {
                failures.addAll(validateRorExists(i, organisation));
            } else {
                // should fail to parse at openapi/spring/jackson, why validate it?
                log.with("value", organisation.getIdentifierSchemeUri()).
                        warn("unexpected organisation id scheme");
                failures.add(organisationInvalidIdScheme(i));
            }
        }

        return failures;
    }

    public List<ValidationFailure> validateRoleFields(
            Supplier<String> fieldPrefix, List<OrganisationRole> roles
    ) {
        var failures = new ArrayList<ValidationFailure>();

        roles.stream().collect(indexed()).forEach((i, iRole) -> {
            if (iRole.getRoleSchemeUri() == null) {
                failures.add(new ValidationFailure().
                        fieldId("%s.roles[%s].roleSchemeUri".
                                formatted(fieldPrefix.get(), i)).
                        errorType(NOT_SET_TYPE).
                        message(FIELD_MUST_BE_SET_MESSAGE));
            }
            if (iRole.getRole() == null) {
                failures.add(new ValidationFailure().
                        fieldId("%s.roles[%s].role".
                                formatted(fieldPrefix.get(), i)).
                        errorType(NOT_SET_TYPE).
                        message(FIELD_MUST_BE_SET_MESSAGE));
            }
        });

        return failures;
    }

    public List<ValidationFailure> validateRorExists(
            int index,
            OrganisationBlock organisation
    ) {
        String id = organisation.getId().trim();
        if (id.length() > ROR_LENGTH) {
            return List.of(organisationInvalidRorFormat(index, "too long"));
        }
        if (id.length() < ROR_LENGTH) {
            return List.of(organisationInvalidRorFormat(index, "too short"));
        }

        return rorService.validate(id, String.format("organisations[%d].id", index));
    }
}