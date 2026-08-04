package com.media.flow.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author yefrosiniya.zinkovskaya
 * @since 04.08.2026
 */
public class DateUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String getCurrentDate() {
        final LocalDate localDate = LocalDate.now();
        return localDate.format(FORMATTER);
    }
}
