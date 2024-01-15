package au.org.raid.api.validator;

import au.org.raid.api.util.DateUtil;
import au.org.raid.idl.raidv2.model.Contributor;
import au.org.raid.idl.raidv2.model.ContributorPosition;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
                                        .fieldId("contributor[%d].schemaUri".formatted(index))
                                        .errorType(NOT_SET_TYPE)
                                        .message(NOT_SET_MESSAGE)
                        );
                    } else if (!contributor.getSchemaUri().equals(ORCID_ORG)) {
                        failures.add(new ValidationFailure()
                                .fieldId("contributor[%d].schemaUri".formatted(index))
                                .errorType(INVALID_VALUE_TYPE)
                                .message(INVALID_VALUE_MESSAGE + " - should be " + ORCID_ORG)
                        );
                    }

                    if (contributor.getPosition().isEmpty()) {
                        failures.add(new ValidationFailure()
                                .fieldId("contributor[%d]".formatted(index))
                                .errorType(NOT_SET_TYPE)
                                .message("A contributor must have a position")
                        );
                    }

                    failures.addAll(orcidValidationService.validate(contributor.getId(), index));

                    IntStream.range(0, contributor.getRole().size())
                            .forEach(roleIndex -> {
                                final var role = contributor.getRole().get(roleIndex);
                                failures.addAll(roleValidationService.validate(role, index, roleIndex));
                            });

                    IntStream.range(0, contributor.getPosition().size())
                            .forEach(positionIndex -> {
                                final var position = contributor.getPosition().get(positionIndex);
                                failures.addAll(positionValidationService.validate(position, index, positionIndex));
                            });

                    failures.addAll(validatePositions(contributor.getPosition(), index));

                });

        failures.addAll(validateLeader(contributors));
        failures.addAll(validateContact(contributors));

        return failures;
    }

    private List<ValidationFailure> validateLeader(
            List<Contributor> contributors
    ) {
        var failures = new ArrayList<ValidationFailure>();

        var leaders = contributors.stream()
                .filter(contributor -> contributor.getLeader() != null && contributor.getLeader())
                .toList();

        if (leaders.isEmpty()) {
            failures.add(new ValidationFailure().
                    fieldId("contributor").
                    errorType(INVALID_VALUE_TYPE).
                    message("At least one contributor must be flagged as a project leader"));
        }

        return failures;
    }

    private List<ValidationFailure> validateContact(
            List<Contributor> contributors
    ) {
        var failures = new ArrayList<ValidationFailure>();

        var leaders = contributors.stream()
                .filter(contributor -> contributor.getContact() != null && contributor.getContact())
                .toList();

        if (leaders.isEmpty()) {
            failures.add(new ValidationFailure().
                    fieldId("contributor").
                    errorType(NOT_SET_TYPE).
                    message("At least one contributor must be flagged as a project contact"));
        }

        return failures;
    }

    private List<ValidationFailure> validateLeadContributors(final List<Contributor> contributors) {
        final var failures = new ArrayList<ValidationFailure>();

        final List<Map<String, Object>> positions = new ArrayList<>();

        for (int contributorIndex = 0; contributorIndex < contributors.size(); contributorIndex++) {
            final var contributor = contributors.get(contributorIndex);

            for (int positionIndex = 0; positionIndex < contributors.get(contributorIndex).getPosition().size(); positionIndex++) {
                final var position = contributor.getPosition().get(positionIndex);

                positions.add(Map.of(
                        "contributorIndex", contributorIndex,
                        "positionIndex", positionIndex,
                        "leader", position.getId().equals(LEADER_POSITION),
                        "startDate", DateUtil.parseDate(position.getStartDate()),
                        "endDate", position.getEndDate() == null ? LocalDate.now() : DateUtil.parseDate(position.getEndDate())
                ));
            }
        }

        List<Map<String, Object>> leaders = positions.stream()
                .filter(map -> (boolean) map.get("leader"))
                .sorted((o1, o2) -> {
                    if (o1.get("startDate").equals(o2.get("startDate"))) {
                        return ((LocalDate) o1.get("endDate")).compareTo((LocalDate) o2.get("endDate"));
                    }
                    return ((LocalDate) o1.get("startDate")).compareTo((LocalDate) o2.get("startDate"));
                })
                .toList();

        for (int i = 1; i < leaders.size(); i++) {
            var previousEntry = leaders.get(i - 1);
            var entry = leaders.get(i);

            final var start = (LocalDate) entry.get("startDate");
            final var previousEnd = (LocalDate) previousEntry.get("endDate");

            if (start.isBefore(previousEnd)) {
                failures.add(new ValidationFailure()
                        .fieldId("contributor[%d].position[%d]".formatted(
                                (int) entry.get("contributorIndex"), (int) entry.get("positionIndex")
                        ))
                        .errorType(INVALID_VALUE_TYPE)
                        .message(String.format("There can only be one leader in any given period. The position at contributor[%d].position[%d] conflicts with this position."
                                .formatted((int) previousEntry.get("contributorIndex"), (int) previousEntry.get("positionIndex")))
                        )
                );
            }
        }

        return failures;
    }

    private List<ValidationFailure> validatePositions(final List<ContributorPosition> positions, final int contributorIndex) {
        final var failures = new ArrayList<ValidationFailure>();
        var sortedPositions = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < positions.size(); i++) {
            final var position = positions.get(i);

            sortedPositions.add(Map.of(
                    "index", i,
                    "start", DateUtil.parseDate(position.getStartDate()),
                    "end", position.getEndDate() == null ? LocalDate.now() : DateUtil.parseDate(position.getEndDate())
                    ));
        }

        sortedPositions.sort((o1, o2) -> {
            if (o1.get("start").equals(o2.get("start"))) {
                return ((LocalDate) o1.get("end")).compareTo((LocalDate) o2.get("end"));
            }
            return ((LocalDate) o1.get("start")).compareTo((LocalDate) o2.get("start"));
        });

        for (int i = 1; i < sortedPositions.size(); i++) {
            final var previousPosition = sortedPositions.get(i - 1);
            final var position = sortedPositions.get(i);

            if (((LocalDate) position.get("start")).isBefore(((LocalDate) previousPosition.get("end")))) {
                failures.add(new ValidationFailure()
                        .fieldId("contributor[%d].position[%d].startDate".formatted(contributorIndex, (int)position.get("index")))
                        .errorType(INVALID_VALUE_TYPE)
                        .message("Contributors can only hold one position at any given time. This position conflicts with contributor[%d].position[%d]"
                                .formatted(contributorIndex, (int) previousPosition.get("index")))
                );

            }
        }

        return failures;
    }
}

