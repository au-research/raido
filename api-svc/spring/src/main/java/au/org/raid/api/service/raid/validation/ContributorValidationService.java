package au.org.raid.api.service.raid.validation;

import au.org.raid.api.service.orcid.OrcidService;
import au.org.raid.api.util.Log;
import au.org.raid.idl.raidv2.model.ContributorBlock;
import au.org.raid.idl.raidv2.model.ContributorPosition;
import au.org.raid.idl.raidv2.model.ContributorRole;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static au.org.raid.api.endpoint.message.ValidationMessage.*;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.ObjectUtil.indexed;
import static au.org.raid.api.util.StringUtil.isBlank;
import static au.org.raid.idl.raidv2.model.ContributorIdentifierSchemeType.HTTPS_ORCID_ORG_;
import static au.org.raid.idl.raidv2.model.ContributorPositionRaidMetadataSchemaType.LEADER;
import static au.org.raid.idl.raidv2.model.ContributorPositionSchemeType.HTTPS_RAID_ORG_;
import static java.util.Collections.emptyList;
import static java.util.List.of;

@Component
public class ContributorValidationService {
    private static final Log log = to(ContributorValidationService.class);

    private final OrcidService orcidSvc;

    public ContributorValidationService(
            final OrcidService orcidSvc
    ) {
        this.orcidSvc = orcidSvc;
    }

    public static ValidationFailure contribInvalidOrcidFormat(
            int index, String message
    ) {
        return new ValidationFailure().
                fieldId("contributors[%s].id".formatted(index)).
                errorType(INVALID_VALUE_TYPE).
                message(message);
    }

    /**
     * Generates check digit as per ISO 7064 11,2.
     * https://web.archive.org/web/20220928212925/https://support.orcid.org/hc/en-us/articles/360006897674-Structure-of-the-ORCID-Identifier
     */
    public static char generateOrcidCheckDigit(String baseDigits) {
        int total = 0;
        for (int i = 0; i < baseDigits.length(); i++) {
            int digit = Character.getNumericValue(baseDigits.charAt(i));
            total = (total + digit) * 2;
        }
        int remainder = total % 11;
        int result = (12 - remainder) % 11;
        return result == 10 ? 'X' : String.valueOf(result).charAt(0);
    }

    public static List<ContributorBlock> getLeaders(
            List<ContributorBlock> contribs
    ) {
        return contribs.stream().filter(
                i -> i.getPositions().stream().anyMatch(j -> j.getPosition() == LEADER)
        ).toList();
    }

    public List<ValidationFailure> validateContributors(
            List<ContributorBlock> contribs
    ) {
        if (contribs == null) {
            return of(CONTRIB_NOT_SET);
        }

        var failures = new ArrayList<ValidationFailure>();

        contribs.stream().
                collect(indexed()).
                forEach((i, iContrib) -> {
                    Supplier<String> fieldPrefix = () -> "contributors[%s]".formatted(i);

                    failures.addAll(validateIdFields(i, iContrib));
                    failures.addAll(
                            validatePositionFields(fieldPrefix, iContrib.getPositions()));
                    failures.addAll(
                            validateRoleFields(fieldPrefix, iContrib.getRoles()));
                    failures.addAll(validateOrcidExists(i, iContrib));
                });

        failures.addAll(validateLeader(contribs));

        return failures;
    }

    public List<ValidationFailure> validateIdFields(
            int i, ContributorBlock iContrib
    ) {
        var failures = new ArrayList<ValidationFailure>();
        if (isBlank(iContrib.getId())) {
            failures.add(contribIdNotSet(i));
        }
        if (iContrib.getIdentifierSchemeUri() == null) {
            failures.add(contribIdSchemeNotSet(i));
        } else {
            if (iContrib.getIdentifierSchemeUri() == HTTPS_ORCID_ORG_) {
                failures.addAll(validateOrcidFormat(i, iContrib));
            } else {
                // should fail to parse at openapi/spring/jackson, why validate it?
                log.with("value", iContrib.getIdentifierSchemeUri()).
                        warn("unexpected contributor id scheme");
                failures.add(contribInvalidIdScheme(i));
            }
        }

        return failures;
    }

    public List<ValidationFailure> validatePositionFields(
            Supplier<String> fieldPrefix, List<ContributorPosition> positions
    ) {
        var failures = new ArrayList<ValidationFailure>();

        for (int j = 0; j < positions.size(); j++) {
            var jPosition = positions.get(j);
            if (jPosition.getPositionSchemaUri() == null) {
                failures.add(new ValidationFailure().
                        fieldId("%s.positions[%s].positionSchemaUri".
                                formatted(fieldPrefix.get(), j)).
                        errorType(NOT_SET_TYPE).
                        message(FIELD_MUST_BE_SET_MESSAGE));
            } else if (jPosition.getPositionSchemaUri() != HTTPS_RAID_ORG_) {
                // should fail to parse at openapi/spring/jackson, why validate it?
                failures.add(new ValidationFailure().
                        fieldId("%s.positions[%s].positionSchemaUri".
                                formatted(fieldPrefix.get(), j)).
                        errorType(INVALID_VALUE_TYPE).
                        message(INVALID_VALUE_MESSAGE));
            }

            if (jPosition.getPosition() == null) {
                failures.add(new ValidationFailure().
                        fieldId("%s.positions[%s].position".
                                formatted(fieldPrefix.get(), j)).
                        errorType(NOT_SET_TYPE).
                        message(FIELD_MUST_BE_SET_MESSAGE));
            }

            if (jPosition.getStartDate() == null) {
                failures.add(new ValidationFailure().
                        fieldId("%s.positions[%s].startDate".
                                formatted(fieldPrefix.get(), j)).
                        errorType(NOT_SET_TYPE).
                        message(FIELD_MUST_BE_SET_MESSAGE));
            }

        }

        return failures;
    }

    public List<ValidationFailure> validateRoleFields(
            Supplier<String> fieldPrefix, List<ContributorRole> roles
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

    public List<ValidationFailure> validateOrcidFormat(
            int index,
            ContributorBlock contrib
    ) {
        String id = contrib.getId().trim();
        if (!id.startsWith(HTTPS_ORCID_ORG_.getValue())) {
            return List.of(contribInvalidOrcidFormat(index, String.format("should start with %s", HTTPS_ORCID_ORG_)));
        }
        if (id.length() > 37) {
            return List.of(contribInvalidOrcidFormat(index, "too long"));
        }
        if (id.length() < 37) {
            return List.of(contribInvalidOrcidFormat(index, "too short"));
        }

        String orcidDigits = id.replaceAll(HTTPS_ORCID_ORG_.getValue(), "").replaceAll("-", "");
        String baseDigits = orcidDigits.substring(0, orcidDigits.length() - 1);
        char checkDigit = orcidDigits.substring(orcidDigits.length() - 1).charAt(0);
        var generatedCheckDigit = generateOrcidCheckDigit(baseDigits);
        if (checkDigit != generatedCheckDigit) {
            return List.of(contribInvalidOrcidFormat(index,
                    "failed checksum, last digit should be `%s`".
                            formatted(generatedCheckDigit)));
        }

        return emptyList();
    }

    private List<ValidationFailure> validateLeader(
            List<ContributorBlock> contribs
    ) {
        var failures = new ArrayList<ValidationFailure>();

        var leaders = getLeaders(contribs);
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

    public List<ValidationFailure> validateOrcidExists(
            final int index,
            final ContributorBlock contributor
    ) {
        return orcidSvc.validate(contributor.getId(), "contributors[%d].id".formatted(index));
    }
}