package br.com.devduo.viverbemapi.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class DateUtils {
    public static String listLocalDateToString(List<LocalDate> dates) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        String formattedMonths = dates.stream()
                .map(date -> date.format(formatter))
                .collect(Collectors.joining(", "));
        return formattedMonths;
    }

    public static LocalDate formatCompetency(Integer dueDate, LocalDate competency) {
        return LocalDate.of(competency.getYear(), competency.getMonth(), dueDate);
    }

    public static int getMonthsBetweenDates(LocalDate startDate, LocalDate endDate) {
        long period = ChronoUnit.MONTHS.between(startDate, endDate) + 1;
        return (int) period;
    }
}
