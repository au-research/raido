package au.org.raid.api.util;


import au.org.raid.api.exception.InvalidDateException;

import java.time.LocalDate;

public class DateUtil {
    public static LocalDate parseDate(final String date) {
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

