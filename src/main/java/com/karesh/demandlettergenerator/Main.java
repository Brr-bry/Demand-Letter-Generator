package com.karesh.demandlettergenerator;

import com.karesh.demandlettergenerator.model.Customer;
import com.karesh.demandlettergenerator.model.Transaction;
import com.karesh.demandlettergenerator.parser.ExcelParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        ExcelParser parser = new ExcelParser();

        try {

            List<Customer> customers =
                    parser.parse(Path.of("input/raw_file.xlsx"));


            System.out.println("Customers Parsed: " + customers.size());

            for (Customer customer : customers) {

                System.out.println("========================================");
                System.out.println(customer.getFullName());

                for (Transaction transaction : customer.getTransactions()) {

                    System.out.println("--------------------------------");

                    System.out.println("SO Number      : " + transaction.getSoNumber());
                    System.out.println("TRA Number     : " + transaction.getTraNumber());
                    System.out.println("Due Date       : " + transaction.getDueDate());
                    System.out.println("Months         : " + transaction.getNumberOfMonths());
                    System.out.println("Unsettled      : " + transaction.getUnsettledAmount());
                    System.out.println("Total Due      : " + transaction.getTotalDue());

                }

                System.out.println();

                System.out.println("Gross Total    : " + customer.getTotalGross());
                System.out.println("With Penalty   : " + customer.getTotalIncludingPenalty());

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}