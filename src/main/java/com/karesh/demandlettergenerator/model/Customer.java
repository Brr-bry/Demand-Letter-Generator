package com.karesh.demandlettergenerator.model;

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

    private BigDecimal totalOverdue;
    private String amountInWords;

    private List<Transaction> transactions = new ArrayList<>();

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

    public BigDecimal getTotalOverdue() {
        return totalOverdue;
    }

    public String getAmountInWords() {
        return amountInWords;
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

    public void setTotalOverdue(BigDecimal totalOverdue) {
        this.totalOverdue = totalOverdue;
    }

    public void setAmountInWords(String amountInWords) {
        this.amountInWords = amountInWords;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
