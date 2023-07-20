package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.Contributor;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static raido.apisvc.endpoint.message.ValidationMessage.*;
import static raido.apisvc.util.StringUtil.isBlank;

@Component
public class StableContributorValidationService {
  private static final String LEADER_POSITION =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/leader.json";

  private final StableOrcidValidationService orcidValidationService;
  private final StableContributorPositionValidationService positionValidationService;
  private final StableContributorRoleValidationService roleValidationService;

  public StableContributorValidationService(final StableOrcidValidationService orcidValidationService,
                                            final StableContributorPositionValidationService positionValidationService,
                                            final StableContributorRoleValidationService roleValidationService) {
    this.orcidValidationService = orcidValidationService;
    this.positionValidationService = positionValidationService;
    this.roleValidationService = roleValidationService;
  }

  public List<ValidationFailure> validate(
    List<Contributor> contributors
  ) {
    if( contributors == null || contributors.isEmpty()) {
      return List.of(CONTRIB_NOT_SET);
    }

    var failures = new ArrayList<ValidationFailure>();

    IntStream.range(0, contributors.size())
      .forEach(contributorIndex -> {
        final var contributor = contributors.get(contributorIndex);

        if (isBlank(contributor.getIdentifierSchemeUri())) {
          failures.add(
            new ValidationFailure()
              .fieldId("contributors[%d].schemeUri".formatted(contributorIndex))
              .errorType(NOT_SET_TYPE)
              .message(FIELD_MUST_BE_SET_MESSAGE)
          );
        }

        failures.addAll(orcidValidationService.validate(contributor.getId(), contributorIndex));

        IntStream.range(0, contributor.getRoles().size())
          .forEach(roleIndex -> {
            final var role = contributor.getRoles().get(roleIndex);
            failures.addAll(roleValidationService.validate(role, contributorIndex, roleIndex));
          });

        IntStream.range(0, contributor.getPositions().size())
          .forEach(positionIndex -> {
            final var position = contributor.getPositions().get(positionIndex);
            failures.addAll(positionValidationService.validate(position, contributorIndex, positionIndex));
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
        .anyMatch(j -> j.getType().equals(LEADER_POSITION))
      ).toList();
  }

  private List<ValidationFailure> validateLeader(
    List<Contributor> contributors
  ){
    var failures = new ArrayList<ValidationFailure>();

    var leaders = getLeaders(contributors);
    if( leaders.isEmpty() ){
      failures.add(new ValidationFailure().
        fieldId("contributors.positions").
        errorType(INVALID_VALUE_TYPE).
        message("leader must be specified"));
    }
    else if( leaders.size() > 1 ){
      failures.add(new ValidationFailure().
        fieldId("contributors.positions").
        errorType(INVALID_VALUE_TYPE).
        message("only one leader can be specified"));
    }

    return failures;
  }
}

