package org.flightsearch.validation;

import org.flightsearch.model.LogResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class LogRequestValidator {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    // 1. Sadece provider zorunlu
    public static void validateProviderOnly(List<String> providers, LogResponse response) {
        if (providers == null || providers.isEmpty()) {
            response.setHasError(Boolean.TRUE);
            response.setErrorMessage("En az bir provider seçilmelidir");
        }
        for (String provider : providers) {
            if (provider == null || provider.isBlank()) {
                response.setHasError(Boolean.TRUE);
                response.setErrorMessage("Provider boş olamaz");
            }
        }
    }

    // 2. Sadece tarih zorunlu
    public static void validateDatesOnly(String start, String end,LogResponse response) {
        if (start == null || end == null || start.isBlank() || end.isBlank()) {
            response.setHasError(Boolean.TRUE);
            response.setErrorMessage("Start ve End tarihleri zorunludur");
        }else {
            try {
                LocalDateTime startDate = LocalDateTime.parse(start, formatter);
                LocalDateTime endDate = LocalDateTime.parse(end, formatter);
                if (startDate.isAfter(endDate)) {
                    response.setHasError(Boolean.TRUE);
                    response.setErrorMessage("Start tarihi, End tarihinden büyük olamaz");
                }
            } catch (DateTimeParseException e) {
                response.setHasError(Boolean.TRUE);
                response.setErrorMessage("Tarih formatı hatalı. ISO format: yyyy-MM-dd'T'HH:mm:ss");
            }
        }
    }

    // 3. Provider ve tarih birlikte zorunlu
    public static void validateProviderAndDates(List<String> providers, String start, String end, LogResponse response) {
        validateProviderOnly(providers,response);
        validateDatesOnly(start, end,response);
    }
}

