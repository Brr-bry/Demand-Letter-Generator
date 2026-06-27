package com.karesh.demandlettergenerator.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    private String soNumber;

    private LocalDate soDate;

    private String traNumber;

    private LocalDate dueDate;

    private String customerNumber;

    private BigDecimal unsettledAmount;

    private int daysLapse;

    private BigDecimal penalty;
}
