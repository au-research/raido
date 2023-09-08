package au.org.raid.api.validator;

import au.org.raid.api.exception.InvalidDateException;
import au.org.raid.idl.raidv2.model.Contributor;
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
            // validate dates

            failures.addAll(validateLeadContributors(contributors));
        }

        return failures;
    }





    private List<ValidationFailure> validateLeadContributors(final List<Contributor> contributors) {
        final var failures = new ArrayList<ValidationFailure>();
        final var today = LocalDate.now();


        final List<Map<String, Object>> positions = new ArrayList<>();

        for (int contributorIndex = 0; contributorIndex < contributors.size(); contributorIndex++) {
            final var contributor = contributors.get(contributorIndex);

            for (int positionIndex = 0; positionIndex < contributors.get(contributorIndex).getPositions().size(); positionIndex++) {
                final var position = contributor.getPositions().get(positionIndex);

                positions.add(Map.of(
                        "contributorIndex", contributorIndex,
                        "positionIndex", positionIndex,
                        "leader", position.getId().equals(LEADER_POSITION),
                        "startDate", position.getStartDate(),
                        "endDate", position.getEndDate()
                ));
            }
        }

        List<Map<String, Object>> leaders = positions.stream()
                .filter(map -> (boolean) map.get("leader"))
                .sorted((o1, o2) -> {
                    if (getDate((String) o1.get("startDate")).equals(getDate((String)o2.get("startDate")))) {
                        return getDate((String) o1.get("endDate")).compareTo(getDate((String) o2.get("endDate")));
                    }
                    return getDate((String) o1.get("startDate")).compareTo(getDate((String) o2.get("startDate")));
                })
                .toList();

        for (int i = 1; i < leaders.size(); i++) {
            var previousEntry = leaders.get(i - 1);
            var entry = leaders.get(i);

            final var start = getDate((String) entry.get("startDate"));
            final var previousEnd = previousEntry.get("endDate") == null ?
                    LocalDate.now() : getDate((String) previousEntry.get("endDate"));

            if (start.isBefore(previousEnd)) {
                failures.add(new ValidationFailure()
                        .fieldId("contributors[%d].positions[%d]".formatted(
                                (int) entry.get("contributorIndex"), (int) entry.get("positionIndex")
                        ))
                        .errorType(INVALID_VALUE_TYPE)
                        .message(String.format("There can only be one leader in any given period. The position at contributors[%d].positions[%d] conflicts with this position."
                                .formatted((int) previousEntry.get("contributorIndex"), (int) previousEntry.get("positionIndex")))
                        )
                );

            }

        }

        return failures;
    }


    private LocalDate getDate(final String date) {

        if (date.matches("\\d{4}")) {
            return LocalDate.of(Integer.parseInt(date), 1, 1);
        } else if (date.matches("\\d{4}-\\d{2}")) {
            var yearMonth = date.split("-");
            return LocalDate.of(Integer.parseInt(yearMonth[0]), Integer.parseInt(yearMonth[1]), 1);
        } else if (date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            var yearMonthDay = date.split("-");
            return LocalDate.of(Integer.parseInt(yearMonthDay[0]), Integer.parseInt(yearMonthDay[1]), Integer.parseInt(yearMonthDay[2]));
        } else {
            throw new InvalidDateException(date);
        }

    }

}

