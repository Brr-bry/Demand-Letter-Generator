package com.karesh.demandlettergenerator.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    private String customerName;
    private String soNumber;
    private LocalDate soDate;
    private String traNumber;
    private LocalDate dueDate;
    private String customerNumber;
    private BigDecimal unsettledAmount;
    private int daysLapse;
    private BigDecimal penalty;
    private int numberOfMonths;
    private BigDecimal totalDue;

    public int getNumberOfMonths() {
        return numberOfMonths;
    }

    public void setNumberOfMonths(int numberOfMonths) {
        this.numberOfMonths = numberOfMonths;
    }

    public void setTotalDue(BigDecimal totalDue) {
        this.totalDue = totalDue;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setSoNumber(String soNumber) {
        this.soNumber = soNumber;
    }

    public void setSoDate(LocalDate soDate) {
        this.soDate = soDate;
    }

    public void setTraNumber(String traNumber) {
        this.traNumber = traNumber;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public void setUnsettledAmount(BigDecimal unsettledAmount) {
        this.unsettledAmount = unsettledAmount;
    }

    public void setDaysLapse(int daysLapse) {
        this.daysLapse = daysLapse;
    }

    public void setPenalty(BigDecimal penalty) {
        this.penalty = penalty;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getSoNumber() {
        return soNumber;
    }

    public LocalDate getSoDate() {
        return soDate;
    }

    public String getTraNumber() {
        return traNumber;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public BigDecimal getUnsettledAmount() {
        return unsettledAmount;
    }

    public int getDaysLapse() {
        return daysLapse;
    }

    public BigDecimal getPenalty() {
        return penalty;
    }

    public BigDecimal getTotalDue() {

        BigDecimal penaltyRate = new BigDecimal("0.03");

        BigDecimal monthlyPenalty =
                unsettledAmount.multiply(penaltyRate);

        BigDecimal totalPenalty =
                monthlyPenalty.multiply(BigDecimal.valueOf(numberOfMonths));

        return unsettledAmount.add(totalPenalty);
    }

    public void calculateTotalDue() {

        BigDecimal penaltyRate = new BigDecimal("0.03");

        BigDecimal monthlyPenalty =
                unsettledAmount.multiply(penaltyRate);

        BigDecimal totalPenalty =
                monthlyPenalty.multiply(BigDecimal.valueOf(numberOfMonths));

        this.totalDue = unsettledAmount.add(totalPenalty);
    }
}
