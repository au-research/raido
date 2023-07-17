package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Service;
import raido.apisvc.repository.TitleTypeRepository;
import raido.apisvc.repository.TitleTypeSchemeRepository;
import raido.idl.raidv2.model.Title;
import raido.idl.raidv2.model.TitleBlock;
import raido.idl.raidv2.model.TitleType;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static raido.apisvc.endpoint.message.ValidationMessage.*;
import static raido.apisvc.util.JooqUtil.valueFits;
import static raido.apisvc.util.StringUtil.isBlank;
import static raido.db.jooq.api_svc.tables.Raid.RAID;

@Service
public class StableTitleValidationService {
  private final TitleTypeSchemeRepository titleTypeSchemeRepository;
  private final TitleTypeRepository titleTypeRepository;

  public StableTitleValidationService(final TitleTypeSchemeRepository titleTypeSchemeRepository, final TitleTypeRepository titleTypeRepository) {
    this.titleTypeSchemeRepository = titleTypeSchemeRepository;
    this.titleTypeRepository = titleTypeRepository;
  }

  private static final String PRIMARY_TITLE_TYPE =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/primary.json";

  private static final String TITLE_SCHEME_URI =
    "https://github.com/au-research/raid-metadata/tree/main/scheme/title/type/v1";

  public List<ValidationFailure> validatePrimaryTitle(
    List<Title> titles
  ) {

    var primaryTitles = getPrimaryTitles(titles);

    if (primaryTitles.size() == 0) {
      return List.of(AT_LEAST_ONE_PRIMARY_TITLE);
    }

    if (primaryTitles.size() > 1) {
      return List.of(TOO_MANY_PRIMARY_TITLE);
    }

    return emptyList();
  }

  public List<ValidationFailure> validateTitles(List<Title> titles) {
    if (titles == null) {
      return List.of(TITLES_NOT_SET);
    }

    var failures = new ArrayList<ValidationFailure>();

    failures.addAll(validatePrimaryTitle(titles));

    for (int i = 0; i < titles.size(); i++) {
      var title = titles.get(i);

      if (isBlank(title.getSchemeUri())) {
        failures.add(new ValidationFailure()
          .fieldId("titles[%s].schemeUri".formatted(i))
          .errorType(NOT_SET_TYPE)
          .message(FIELD_MUST_BE_SET_MESSAGE));
      } else {
        final var titleTypeScheme = titleTypeSchemeRepository.findByUri(title.getSchemeUri());

        if (titleTypeScheme.isEmpty()) {
          failures.add(new ValidationFailure()
            .fieldId("titles[%s].schemeUri".formatted(i))
            .errorType(INVALID_VALUE_TYPE)
            .message(INVALID_VALUE_MESSAGE));
        } else if (title.getType() != null && titleTypeRepository.findByUriAndSchemeId(title.getType(), titleTypeScheme.get().getId()).isEmpty()) {
          failures.add(new ValidationFailure()
            .fieldId("titles[%s].type".formatted(i))
            .errorType(INVALID_VALUE_TYPE)
            .message(INVALID_VALUE_MESSAGE));
        }
      }

      if (isBlank(title.getTitle())) {
        failures.add(titleNotSet(i));
      }
      if (!valueFits(RAID.PRIMARY_TITLE, title.getTitle())) {
        failures.add(primaryTitleTooLong(i));
      }
      if (isBlank(title.getType())) {
        failures.add(titlesTypeNotSet(i));
      }
      if (title.getStartDate() == null) {
        failures.add(titleStartDateNotSet(i));
      }

    }
    return failures;
  }


  public List<Title> getPrimaryTitles(List<Title> titles) {
    return titles.stream().filter(title ->
      title.getType() != null && title.getType().equals(PRIMARY_TITLE_TYPE)
    ).toList();
  }

  /**
   * Should only call be called with validated metadata.
   *
   * @throws java.util.NoSuchElementException if no primary title is present
   */
  public TitleBlock getPrimaryTitle(List<TitleBlock> titles) {
    return titles.stream()
      .filter(i -> i.getType() == TitleType.PRIMARY_TITLE)
      .findFirst().orElseThrow();
  }

}
