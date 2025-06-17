package com.example.myCourse.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * When reading JSON:
 *   • Try "MMMM d, yyyy" (e.g. "June 1, 2025")
 *   • If that fails, try ISO ("yyyy-MM-dd").
 * When writing JSON, always emit "MMMM d, yyyy".
 */
public class LocalDateAdapter extends TypeAdapter<LocalDate> {
    // Human-friendly: e.g. "June 1, 2025"
    private static final DateTimeFormatter HUMAN_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);

    // ISO fallback: "yyyy-MM-dd"
    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.format(HUMAN_FORMATTER));
        }
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        String str = in.nextString();
        if (str == null || str.isEmpty()) {
            return null;
        }
        // Try human first:
        try {
            return LocalDate.parse(str, HUMAN_FORMATTER);
        } catch (DateTimeParseException e) {
            // Fallback to ISO
            try {
                return LocalDate.parse(str, ISO_FORMATTER);
            } catch (DateTimeParseException e2) {
                throw new JsonParseException("Cannot parse date: \"" + str + "\"", e2);
            }
        }
    }
}
