package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.apisvc.repository.ContributorRoleTypeRepository;
import raido.apisvc.repository.ContributorRoleTypeSchemeRepository;
import raido.idl.raidv2.model.ContribRole;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;

import static raido.apisvc.endpoint.message.ValidationMessage.*;
import static raido.apisvc.util.StringUtil.isBlank;

@Component
public class StableContributorRoleValidationService {
  private final ContributorRoleTypeSchemeRepository contributorRoleTypeSchemeRepository;
  private final ContributorRoleTypeRepository contributorRoleTypeRepository;

  public StableContributorRoleValidationService(final ContributorRoleTypeSchemeRepository contributorRoleTypeSchemeRepository, final ContributorRoleTypeRepository contributorRoleTypeRepository) {
    this.contributorRoleTypeSchemeRepository = contributorRoleTypeSchemeRepository;
    this.contributorRoleTypeRepository = contributorRoleTypeRepository;
  }

  public List<ValidationFailure> validate(
    final ContribRole role, final int contributorIndex, final int roleIndex) {
    final var failures = new ArrayList<ValidationFailure>();

    if (isBlank(role.getRole())) {
      failures.add(
        new ValidationFailure()
          .fieldId("contributors[%d].roles[%d].role".formatted(contributorIndex, roleIndex))
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
        contributorRoleTypeSchemeRepository.findByUri(role.getSchemeUri());

      if (roleScheme.isEmpty()) {
        failures.add(
          new ValidationFailure()
            .fieldId("contributors[%d].roles[%d].schemeUri".formatted(contributorIndex, roleIndex))
            .errorType(INVALID_VALUE_TYPE)
            .message(INVALID_VALUE_MESSAGE)
        );
      } else if (!isBlank(role.getRole()) &&
        contributorRoleTypeRepository.findByUriAndSchemeId(role.getRole(), roleScheme.get().getId()).isEmpty()) {
        failures.add(
          new ValidationFailure()
            .fieldId("contributors[%d].roles[%d].role".formatted(contributorIndex, roleIndex))
            .errorType(INVALID_VALUE_TYPE)
            .message(INVALID_VALUE_MESSAGE)
        );
      }
    }

    return failures;
  }
}
