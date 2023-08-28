package au.org.raid.api.validator;

import au.org.raid.idl.raidv2.model.Contributor;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static au.org.raid.api.endpoint.message.ValidationMessage.*;
import static au.org.raid.api.util.StringUtil.isBlank;

@Component
public class ContributorValidator {
    private static final String ORCID_ORG = "https://orcid.org/";
    private static final String LEADER_POSITION =
            "https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/leader.json";
    private final OrcidValidator orcidValidationService;
    private final ContributorPositionValidator positionValidationService;
    private final ContributorRoleValidator roleValidationService;

    public ContributorValidator(final OrcidValidator orcidValidationService,
                                final ContributorPositionValidator positionValidationService,
                                final ContributorRoleValidator roleValidationService) {
        this.orcidValidationService = orcidValidationService;
        this.positionValidationService = positionValidationService;
        this.roleValidationService = roleValidationService;
    }

    public List<ValidationFailure> validate(
            List<Contributor> contributors
    ) {
        if (contributors == null || contributors.isEmpty()) {
            return List.of(CONTRIB_NOT_SET);
        }

        var failures = new ArrayList<ValidationFailure>();

        IntStream.range(0, contributors.size())
                .forEach(index -> {
                    final var contributor = contributors.get(index);

                    if (isBlank(contributor.getSchemaUri())) {
                        failures.add(
                                new ValidationFailure()
                                        .fieldId("contributors[%d].schemaUri".formatted(index))
                                        .errorType(NOT_SET_TYPE)
                                        .message(NOT_SET_MESSAGE)
                        );
                    } else if (!contributor.getSchemaUri().equals(ORCID_ORG)) {
                        failures.add(new ValidationFailure()
                                .fieldId("contributors[%d].schemaUri".formatted(index))
                                .errorType(INVALID_VALUE_TYPE)
                                .message(INVALID_VALUE_MESSAGE + " - should be " + ORCID_ORG)
                        );
                    }

                    failures.addAll(orcidValidationService.validate(contributor.getId(), index));

                    IntStream.range(0, contributor.getRoles().size())
                            .forEach(roleIndex -> {
                                final var role = contributor.getRoles().get(roleIndex);
                                failures.addAll(roleValidationService.validate(role, index, roleIndex));
                            });

                    IntStream.range(0, contributor.getPositions().size())
                            .forEach(positionIndex -> {
                                final var position = contributor.getPositions().get(positionIndex);
                                failures.addAll(positionValidationService.validate(position, index, positionIndex));
                            });

                });

        failures.addAll(validateLeader(contributors));

        return failures;
    }

    public List<Contributor> getLeaders(
            List<Contributor> contributors
    ) {
        return contributors.stream()
                .filter(i -> i.getPositions()
                        .stream()
                        .anyMatch(j -> j.getId().equals(LEADER_POSITION))
                ).toList();
    }

    private List<ValidationFailure> validateLeader(
            List<Contributor> contributors
    ) {
        var failures = new ArrayList<ValidationFailure>();

        var leaders = getLeaders(contributors);
        if (leaders.isEmpty()) {
            failures.add(new ValidationFailure().
                    fieldId("contributors.positions").
                    errorType(INVALID_VALUE_TYPE).
                    message("leader must be specified"));
        } else if (leaders.size() > 1) {
            failures.add(new ValidationFailure().
                    fieldId("contributors.positions").
                    errorType(INVALID_VALUE_TYPE).
                    message("only one leader can be specified"));
        }

        return failures;
    }
}

