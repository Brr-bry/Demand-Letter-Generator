package com.karesh.demandlettergenerator.util;

import com.karesh.demandlettergenerator.model.Customer;

public class FileNameUtils {

    public static String build(Customer customer, int fileNo) {

        String lastName = sanitize(customer.getLastName());

        String givenNames = extractGivenNames(
                customer.getFullName(),
                customer.getLastName());

        return String.format(
                "%s_%s_%03d.docx",
                lastName,
                givenNames,
                fileNo
        );
    }

    private static String extractGivenNames(
            String fullName,
            String lastName) {

        if (fullName == null || fullName.isBlank()) {
            return "UNKNOWN";
        }

        if (lastName == null || lastName.isBlank()) {
            return sanitize(fullName);
        }

        String name = fullName.trim();

        // Remove the last occurrence of the last name
        int index = name.toLowerCase()
                .lastIndexOf(lastName.toLowerCase());

        if (index != -1) {
            name = name.substring(0, index).trim();
        }

        return sanitize(name);
    }

    private static String sanitize(String text) {

        if (text == null || text.isBlank()) {
            return "UNKNOWN";
        }

        return text.trim()
                .replace(".", "")
                .replaceAll("\\s+", "_")
                .replaceAll("[\\\\/:*?\"<>|]", "")
                .toUpperCase();
    }
}