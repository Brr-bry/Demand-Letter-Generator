package com.karesh.demandlettergenerator.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberToWords {

    private static final String[] ONES = {
            "", "ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX",
            "SEVEN", "EIGHT", "NINE", "TEN", "ELEVEN", "TWELVE",
            "THIRTEEN", "FOURTEEN", "FIFTEEN", "SIXTEEN",
            "SEVENTEEN", "EIGHTEEN", "NINETEEN"
    };

    private static final String[] TENS = {
            "", "", "TWENTY", "THIRTY", "FORTY", "FIFTY",
            "SIXTY", "SEVENTY", "EIGHTY", "NINETY"
    };

    public static String convert(BigDecimal amount) {

        amount = amount.setScale(2, RoundingMode.HALF_UP);

        long pesos = amount.longValue();

        int cents = amount
                .subtract(BigDecimal.valueOf(pesos))
                .movePointRight(2)
                .intValue();

        String words = convertNumber(pesos);

        if (cents == 0) {
            return words + " PESOS";
        }

        return words + " PESOS & "
                + String.format("%02d", cents)
                + "/100";
    }

    private static String convertNumber(long number) {

        if (number == 0) {
            return "ZERO";
        }

        if (number >= 1_000_000_000L) {
            throw new IllegalArgumentException("Maximum supported value is 999,999,999.99");
        }

        StringBuilder builder = new StringBuilder();

        if (number >= 1_000_000) {

            builder.append(convertBelowThousand((int) (number / 1_000_000)))
                    .append(" MILLION ");

            number %= 1_000_000;
        }

        if (number >= 1000) {

            builder.append(convertBelowThousand((int) (number / 1000)))
                    .append(" THOUSAND ");

            number %= 1000;
        }

        if (number > 0) {

            builder.append(convertBelowThousand((int) number));

        }

        return builder.toString().trim().replaceAll("\\s+", " ");
    }

    private static String convertBelowThousand(int number) {

        StringBuilder builder = new StringBuilder();

        if (number >= 100) {

            builder.append(ONES[number / 100])
                    .append(" HUNDRED ");

            number %= 100;
        }

        if (number >= 20) {

            builder.append(TENS[number / 10]);

            if (number % 10 != 0) {
                builder.append("-").append(ONES[number % 10]);
            }

        } else if (number > 0) {

            builder.append(ONES[number]);

        }

        return builder.toString().trim();
    }

}