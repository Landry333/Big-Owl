package com.example.bigowlapp.utils;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

public class DateTimeFormatterTest {

    private final Calendar calendar = Calendar.getInstance();

    @Before
    public void setUp() {
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 23);
        calendar.set(Calendar.YEAR, 2023);

        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 30);
    }

    @Test
    public void dateFormatter() {
        assertEquals("2/23/2023", DateTimeFormatter.dateFormatter(calendar));
    }

    @Test
    public void timeFormatter() {
        assertEquals("15:30", DateTimeFormatter.timeFormatter(calendar));
    }

    @Test
    public void doubleDigitFormatter() {
        // Single Digit Case
        assertEquals("06", DateTimeFormatter.doubleDigitFormatter(6));

        // Double Digit Case
        assertEquals("12", DateTimeFormatter.doubleDigitFormatter(12));

        // Edge Cases
        assertEquals("09", DateTimeFormatter.doubleDigitFormatter(9));
        assertEquals("10", DateTimeFormatter.doubleDigitFormatter(10));
    }
}