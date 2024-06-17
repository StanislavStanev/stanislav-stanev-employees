package com.example.pair_employees.Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static LocalDate getIsoDate(String s) {
        try {
            return LocalDate.parse(s, DateTimeFormatter.ISO_DATE);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
}
