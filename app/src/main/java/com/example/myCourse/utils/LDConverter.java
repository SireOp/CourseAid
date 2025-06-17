package com.example.myCourse.utils;

import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class LDConverter {
    // Human-readable pattern: e.g. "June 1, 2025"
    private static final DateTimeFormatter HUMAN_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);

    // Fallback ISO formatter: "yyyy-MM-dd"
    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE;

    @TypeConverter
    public static LocalDate fromString(String value) {
        if (value == null) return null;

        // Try the human format first
        try {
            return LocalDate.parse(value, HUMAN_FORMATTER);
        } catch (DateTimeParseException e) {
            // Fallback to ISO
            try {
                return LocalDate.parse(value, ISO_FORMATTER);
            } catch (DateTimeParseException e2) {
                // Give up
                return null;
            }
        }
    }

    @TypeConverter
    public static String toString(LocalDate date) {
        if (date == null) return null;
        // Always write out as "June 1, 2025"
        return date.format(HUMAN_FORMATTER);
    }
}
