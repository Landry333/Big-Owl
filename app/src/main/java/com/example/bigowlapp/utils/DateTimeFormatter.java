package com.example.bigowlapp.utils;

import androidx.annotation.VisibleForTesting;

import java.util.Calendar;

public class DateTimeFormatter {
    public static String dateFormatter(Calendar calendar) {
        return (calendar.get(Calendar.MONTH) + 1) + "/" +
                calendar.get(Calendar.DAY_OF_MONTH) + "/" +
                calendar.get(Calendar.YEAR);
    }

    public static String timeFormatter(Calendar calendar) {
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return doubleDigitFormatter(hourOfDay) + ":" + doubleDigitFormatter(minute);
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public static String doubleDigitFormatter(int num) {
        return num < 10 ? ("0" + num) : String.valueOf(num);
    }
}
