package br.com.devduo.viverbemapi.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class DateFormatter {
    public static String listLocalDateToString(List<LocalDate> dates) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        String formattedMonths = dates.stream()
                .map(date -> date.format(formatter))
                .collect(Collectors.joining(", "));
        return formattedMonths;
    }
}
