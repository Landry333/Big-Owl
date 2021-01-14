package com.example.bigowlapp.utils;

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

        return (hourOfDay < 10 ? ("0" + hourOfDay) : hourOfDay) + ":" +
                (minute < 10 ? ("0" + minute) : minute);
    }
}
