package au.org.raid.api.validator;

import au.org.raid.idl.raidv2.model.Organisation;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static au.org.raid.api.endpoint.message.ValidationMessage.*;
import static au.org.raid.api.util.StringUtil.isBlank;

@Component
public class OrganisationValidator {
    private static final String ROR_SCHEMA_URI = "https://ror.org/";
    private final RorValidator rorValidator;
    private final OrganisationRoleValidator roleValidationService;

    public OrganisationValidator(final RorValidator rorValidator,
                                 final OrganisationRoleValidator roleValidationService) {
        this.rorValidator = rorValidator;
        this.roleValidationService = roleValidationService;
    }

    public List<ValidationFailure> validate(
            List<Organisation> organisations
    ) {

    /* organisations has been confirmed as optional in the metadata schema,
    rationale: an ORCID is quick to create (minutes), RORs can take months. */
        if (organisations == null) {
            return Collections.emptyList();
        }

        var failures = new ArrayList<ValidationFailure>();


        IntStream.range(0, organisations.size()).forEach(organisationIndex -> {
            final var organisation = organisations.get(organisationIndex);

            if (isBlank(organisation.getSchemaUri())) {
                failures.add(new ValidationFailure()
                        .fieldId("organisation[%d].schemaUri".formatted(organisationIndex))
                        .errorType(NOT_SET_TYPE)
                        .message(NOT_SET_MESSAGE)
                );
            } else if (!organisation.getSchemaUri().equals(ROR_SCHEMA_URI)) {
                failures.add(new ValidationFailure()
                        .fieldId("organisation[%d].schemaUri")
                        .errorType(INVALID_VALUE_TYPE)
                        .message(INVALID_VALUE_MESSAGE)
                );
            }

            failures.addAll(rorValidator.validate(organisation.getId(), organisationIndex));

            IntStream.range(0, organisation.getRole().size()).forEach(roleIndex -> {
                final var role = organisation.getRole().get(roleIndex);
                failures.addAll(roleValidationService.validate(role, organisationIndex, roleIndex));
            });
        });

        return failures;
    }
}

