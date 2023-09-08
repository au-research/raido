package au.org.raid.api.validator;

import au.org.raid.api.model.DateRange;
import au.org.raid.idl.raidv2.model.ValidationFailure;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static au.org.raid.api.endpoint.message.ValidationMessage.*;

public class DateRangeValidator {
    private List<ValidationFailure> validate(final List<DateRange> dateRanges,
                                             final String parentField,
                                             final Predicate<? super DateRange> filter) {
        final var failures = new ArrayList<ValidationFailure>();
        final var today = LocalDate.now();

        var filteredAndSorted = dateRanges.stream()
                .filter(filter)
                .sorted((o1, o2) -> {
                    if (o1.getStartDate().equals(o2.getStartDate())) {
                        final var o1EndDate = o1.getEndDate() == null ? LocalDate.now() : o1.getEndDate();
                        final var o2EndDate = o2.getEndDate() == null ? LocalDate.now() : o2.getEndDate();

                        return o1EndDate.compareTo(o2EndDate);
                    }
                    return o1.getStartDate().compareTo(o2.getStartDate());
                })
                .collect(Collectors.toCollection(ArrayList::new));

        for (int i = 1; i < filteredAndSorted.size(); i++) {
            final var previous = filteredAndSorted.get(i - 1);
            final var title = filteredAndSorted.get(i);
            final var previousIndex = dateRanges.indexOf(previous);
            final var index = dateRanges.indexOf(title);

            final var endDate = (previous.getEndDate() != null) ? previous.getEndDate() : today;

            if (title.equals(previous)) {
                return List.of(new ValidationFailure()
                        .fieldId("%s[%d]".formatted(parentField, index))
                        .errorType(DUPLICATE_TYPE)
                        .message(DUPLICATE_MESSAGE)
                );
            } else if (title.getStartDate().isBefore(endDate)) {
                failures.add(new ValidationFailure()
                        .fieldId("%s[%d].startDate".formatted(parentField, index))
                        .errorType(INVALID_VALUE_TYPE)
                        .message("There can only be one primary title in any given period. The start date for this title overlaps with titles[%d]".formatted(previousIndex))
                );

            }
        }

        return failures;
    }

}
