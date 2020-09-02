package com.clyde.sherlock.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private final static String DATE_TIME_PATTERN = "yyyy-MM-dd";

    public static String getCurrentDate() {
        return getPastDateByDifferenceInDays(0);
    }

    public static String getPastDateByDifferenceInDays(final int differenceInDays) {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        final LocalDate todayDate = LocalDate.now();

        return dateTimeFormatter.format(todayDate.plusDays(-1 * differenceInDays));
    }
}
