package au.org.raid.api.validator;

import au.org.raid.idl.raidv2.model.Title;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static au.org.raid.api.endpoint.message.ValidationMessage.*;
import static au.org.raid.api.util.JooqUtil.valueFits;
import static au.org.raid.api.util.StringUtil.isBlank;
import static au.org.raid.db.jooq.api_svc.tables.Raid.RAID;
import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class TitleValidator {
    private static final String PRIMARY_TITLE_TYPE =
            "https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/primary.json";

    private final TitleTypeValidator titleTypeValidationService;
    private final LanguageValidator languageValidator;


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

    public List<ValidationFailure> validate(List<Title> titles) {
        if (titles == null) {
            return List.of(TITLES_NOT_SET);
        }

        var failures = new ArrayList<ValidationFailure>();

        failures.addAll(validatePrimaryTitle(titles));

        IntStream.range(0, titles.size()).forEach(index -> {
            ;
            var title = titles.get(index);

            if (isBlank(title.getTitle())) {
                failures.add(titleNotSet(index));
            }
            if (!valueFits(RAID.PRIMARY_TITLE, title.getTitle())) {
                failures.add(primaryTitleTooLong(index));
            }
            if (title.getStartDate() == null) {
                failures.add(titleStartDateNotSet(index));
            }

            failures.addAll(titleTypeValidationService.validate(title.getType(), index));

            failures.addAll(languageValidator.validate(title.getLanguage(), "titles[%d]".formatted(index)));
        });
        return failures;
    }

    public List<Title> getPrimaryTitles(List<Title> titles) {
        return titles.stream().filter(title ->
                title.getType().getId() != null && title.getType().getId().equals(PRIMARY_TITLE_TYPE)
        ).toList();
    }
}