package com.example.demo.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static String getCurrentDate() {
        return getPastDateByDifferenceInDays(0);
    }

    public static String getPastDateByDifferenceInDays(int differenceInDays) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();

        return dtf.format(today.plusDays(-1 * differenceInDays));
    }
}
