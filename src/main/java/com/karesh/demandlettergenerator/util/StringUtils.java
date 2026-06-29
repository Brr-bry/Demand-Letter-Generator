package com.karesh.demandlettergenerator.util;

public class StringUtils {

    public static String toTitleCase(String text) {

        if (text == null || text.isBlank()) {
            return "";
        }

        String[] words = text.toLowerCase().split("\\s+");

        StringBuilder builder = new StringBuilder();

        for (String word : words) {

            builder.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");

        }

        return builder.toString().trim();
    }
}