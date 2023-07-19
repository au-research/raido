package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.apisvc.repository.ContributorRoleRepository;
import raido.apisvc.repository.ContributorRoleSchemeRepository;
import raido.idl.raidv2.model.ContribRole;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;

import static raido.apisvc.endpoint.message.ValidationMessage.*;
import static raido.apisvc.util.StringUtil.isBlank;

@Component
public class StableContributorRoleValidationService {
  private final ContributorRoleSchemeRepository contributorRoleSchemeRepository;
  private final ContributorRoleRepository contributorRoleRepository;

  public StableContributorRoleValidationService(final ContributorRoleSchemeRepository contributorRoleSchemeRepository, final ContributorRoleRepository contributorRoleRepository) {
    this.contributorRoleSchemeRepository = contributorRoleSchemeRepository;
    this.contributorRoleRepository = contributorRoleRepository;
  }

  public List<ValidationFailure> validate(
    final ContribRole role, final int contributorIndex, final int roleIndex) {
    final var failures = new ArrayList<ValidationFailure>();

    if (isBlank(role.getType())) {
      failures.add(
        new ValidationFailure()
          .fieldId("contributors[%d].roles[%d].type".formatted(contributorIndex, roleIndex))
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
            .message(INVALID_VALUE_MESSAGE)
        );
      } else if (!isBlank(role.getType()) &&
        contributorRoleRepository.findByUriAndSchemeId(role.getType(), roleScheme.get().getId()).isEmpty()) {
        failures.add(
          new ValidationFailure()
            .fieldId("contributors[%d].roles[%d].type".formatted(contributorIndex, roleIndex))
            .errorType(INVALID_VALUE_TYPE)
            .message(INVALID_VALUE_MESSAGE)
        );
      }
    }

    return failures;
  }
}
