package com.cumulocity.alarmapp.util;

public class StringUtil {
    public static String toCamelCase(String text) {
        if (text != null && !text.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append(text.substring(0, 1).toUpperCase());
            builder.append(text.substring(1).toLowerCase());
            return builder.toString();
        }
        return text;
    }
}
