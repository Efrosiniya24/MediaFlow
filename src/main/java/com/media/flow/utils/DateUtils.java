package com.media.flow.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 04.08.2026
 */
public class DateUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Gets today date in format yyyy-MM-dd
     *
     * @return today date in format yyyy-MM-dd
     */
    public static String getCurrentDate() {
        final LocalDate localDate = LocalDate.now();
        return localDate.format(FORMATTER);
    }

    /**
     * Gets yesterday date in format yyyy-MM-dd
     *
     * @return yesterday date in format yyyy-MM-dd
     */
    public static String getYesterdayDate() {
        final LocalDate localDate = LocalDate.now().minusDays(1);
        return localDate.format(FORMATTER);
    }
}
