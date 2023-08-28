package ru.vladimir.sazonov.dispatchLog.format;

import lombok.NonNull;
import org.springframework.format.Formatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateFormatter implements Formatter<LocalDateTime> {
    @Override
    public @NonNull LocalDateTime parse(@NonNull String text, @NonNull Locale locale) {
        return LocalDateTime.parse(text, DateTimeFormatter.ISO_DATE_TIME);
    }

    @Override
    public @NonNull String print(@NonNull LocalDateTime object, @NonNull Locale locale) {
        return object.format(DateTimeFormatter.ISO_LOCAL_DATE) + "T" +
                object.format(DateTimeFormatter.ISO_LOCAL_TIME) ;
    }
}
