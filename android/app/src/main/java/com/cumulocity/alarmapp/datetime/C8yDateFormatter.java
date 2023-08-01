package com.cumulocity.alarmapp.datetime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class C8yDateFormatter {

    private static final DateFormat alarmDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final DateFormat printFormat = new SimpleDateFormat("MMM dd yyyy, HH:mm");

    private C8yDateFormatter() {
        // static
    }

    public static String toReadableDate(final String dateTime) {
        try {
            final Date date = alarmDateFormat.parse(dateTime);
            return toReadableDate(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;
        }
    }

    public static String toReadableDate(final Date date) {
        return printFormat.format(date);
    }
}
