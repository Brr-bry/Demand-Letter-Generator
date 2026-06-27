package com.karesh.demandlettergenerator.model;

import com.karesh.demandlettergenerator.util.NumberToWords;

import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

public class Customer {
    private String branch;
    private String fullName;

    private String firstName;
    private String lastName;
    private String address;
    private String phone;
    private int highestNumberOfMonths;
    private BigDecimal totalIncludingPenalty;

    private BigDecimal totalGross;

    public void setHighestNumberOfMonths(int highestNumberOfMonths) {
        this.highestNumberOfMonths = highestNumberOfMonths;
    }

    public int getHighestNumberOfMonths() {
        return highestNumberOfMonths;
    }

    public void setTotalGross(BigDecimal totalGross) {
        this.totalGross = totalGross;
    }

    public void setTotalIncludingPenalty(BigDecimal totalIncludingPenalty) {
        this.totalIncludingPenalty = totalIncludingPenalty;
    }

    public BigDecimal getTotalIncludingPenalty() {
        return totalIncludingPenalty;
    }

    public BigDecimal getTotalGross() {
        return totalGross;
    }


    private List<Transaction> transactions = new ArrayList<>();

    public String getTotalInWords() {
        return NumberToWords.convert(totalIncludingPenalty);
    }

    public String getBranch() {
        return branch;
    }

    public String getFullName() {
        return fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }



    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
