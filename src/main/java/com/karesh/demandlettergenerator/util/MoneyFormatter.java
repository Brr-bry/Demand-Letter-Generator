package com.karesh.demandlettergenerator.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MoneyFormatter {

    private static final DecimalFormat FORMAT =
            new DecimalFormat("#,##0.00");

    public static String format(BigDecimal amount) {

        if (amount == null) {
            return "0.00";
        }

        return FORMAT.format(amount);
    }

}