package com.karesh.demandlettergenerator;

import com.karesh.demandlettergenerator.model.Customer;
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

                System.out.println("--------------------------------");
                System.out.println("Name: " + customer.getFullName());
                System.out.println("Phone: " + customer.getPhone());
                System.out.println("Address: " + customer.getAddress());

                System.out.println("Transactions: " +
                        customer.getTransactions().size());

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}