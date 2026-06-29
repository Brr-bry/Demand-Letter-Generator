package com.karesh.demandlettergenerator.parser;

import com.karesh.demandlettergenerator.model.Customer;
import com.karesh.demandlettergenerator.model.Transaction;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;


public class ExcelParser {
    private final DataFormatter formatter = new DataFormatter();
    private final String CUSTOMER_MARKER = "Dear Mr/Ms/Mrs.:";
    private final String TABLE_MARKER = "SO No.";
    private final String TOTAL_MARKER = "Total Overdue including penalties:";



    private int customersFound;

    public void setCustomersFound(int customersFound) {
        this.customersFound = customersFound;
    }

    public int getCustomersFound() {
        return customersFound;
    }

    public List<Customer> parse(Path excelFile) throws IOException {

        List<Customer> customers = new ArrayList<>();

        try (InputStream inputStream = Files.newInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            ParserState state = ParserState.SEARCHING_CUSTOMER;

            Customer currentCustomer = null;

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                switch (state) {

                    case SEARCHING_CUSTOMER:

                        if (containsText(row, CUSTOMER_MARKER)) {

                            customersFound++;

                            System.out.println("--------------------------------");
                            System.out.println("Customer " + customersFound + " Found");

                            currentCustomer = new Customer();

                            // Dynamically extract customer details
                            extractCustomerDetails(sheet, i, currentCustomer);

                            // Last name is still read from the Dear row
                            currentCustomer.setLastName(
                                    getCellValue(sheet.getRow(i), 4));

                            System.out.println(currentCustomer.getFullName());
                            System.out.println(currentCustomer.getPhone());
                            System.out.println(currentCustomer.getAddress());

                            state = ParserState.WAITING_FOR_TRANSACTION_HEADER;
                        }

                        break;

                    case WAITING_FOR_TRANSACTION_HEADER:

                        if (containsText(row, TABLE_MARKER)) {

                            System.out.println("Transaction table found.");

                            state = ParserState.READING_TRANSACTIONS;
                        }

                        break;

                    case READING_TRANSACTIONS:

                        if (containsText(row, TOTAL_MARKER)) {
                            currentCustomer.calculateTotalGross();
                            currentCustomer.calculateHighestNumberOfMonths();
                            currentCustomer.calculateTotalIncludingPenalty();
                            customers.add(currentCustomer);

                            System.out.println("Customer Completed");

                            currentCustomer = null;

                            state = ParserState.SEARCHING_CUSTOMER;

                            break;
                        }

                        if (isTransactionRow(row)) {

                            Transaction transaction = new Transaction();

                            transaction.setSoNumber(
                                    getCellValue(row, 2));

                            transaction.setSoDate(
                                    parseDate(getCellValue(row, 3)));

                            transaction.setTraNumber(
                                    getCellValue(row, 7));

                            transaction.setDueDate(
                                    parseDate(getCellValue(row, 10)));

                            transaction.setCustomerName(
                                    getCellValue(row, 12));

                            transaction.setCustomerNumber(
                                    getCellValue(row, 14));

                            transaction.setUnsettledAmount(
                                    parseMoney(getCellValue(row, 17)));

                            transaction.setDaysLapse(
                                    parseInteger(getCellValue(row, 19)));

                            transaction.setPenalty(
                                    parseMoney(getCellValue(row, 20)));

                            transaction.setNumberOfMonths(
                                    calculateNumberOfMonths(transaction.getDueDate()));

                            transaction.getTotalDue();

                            currentCustomer.getTransactions().add(transaction);

                        }

                        break;
                }
            }

        }

        return customers;
    }

    private void extractCustomerDetails(
            Sheet sheet,
            int dearRow,
            Customer customer) {

        String phone = "";

        List<String> values = new ArrayList<>();

        for (int row = dearRow - 1;
             row >= 0 && row >= dearRow - 20;
             row--) {


            String value = getCellValue(sheet.getRow(row), 2).trim();
            System.out.println("ROW " + row + " = [" + value + "]");
            if (value.isBlank()) {
                continue;
            }

            if (value.equalsIgnoreCase(
                    "Kindly give this letter preferential attention.")) {
                break;
            }

            if (phone.isEmpty() && looksLikePhone(value)) {
                phone = value.startsWith("0") ? value : "0" + value;
                continue;
            }
            values.add(value);


        }

        String address = "";
        String fullName = "";

        if (values.size() >= 1) {
            address = values.get(0);
        }

        if (values.size() >= 2) {
            address = values.get(values.size() - 2);
            fullName = values.get(values.size() - 1);
        }

        customer.setAddress(address);
        customer.setPhone(phone);
        customer.setFullName(fullName);
    }

    private boolean looksLikePhone(String value) {

        String digits =
                value.replaceAll("\\D", "");

        return digits.length() >= 7;
    }

    private String cleanText(String value) {

        if (value == null) {
            return "";
        }

        return value
                .trim()
                .replaceFirst("^\\\\\\s*", "")   // leading backslashes
                .replaceAll("\\s+", " ");        // multiple spaces
    }

    private String getCellValue(Row row, int columnIndex) {

        if (row == null) {
            return "";
        }

        Cell cell = row.getCell(columnIndex);

        if (cell == null) {
            return "";
        }
        return cleanText(formatter.formatCellValue(cell));
    }


    private boolean containsText(Row row, String text) {

        if (row == null) {
            return false;
        }

        for (Cell cell : row) {

            String value = formatter.formatCellValue(cell);

            if (value.contains(text)) {
                return true;
            }
        }

        return false;
    }

    private boolean isTransactionRow(Row row) {

        if (row == null) {
            return false;
        }

        String soNumber = getCellValue(row, 2);
        String soDate = getCellValue(row, 3);
        String traNumber = getCellValue(row, 7);

        if (soNumber.isBlank() || soDate.isBlank() || traNumber.isBlank()) {
            return false;
        }

        return soNumber.matches("\\d+")
                && traNumber.matches("\\d+");
    }

    private BigDecimal parseMoney(String value) {

        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }

        value = value.replace(",", "").trim();

        return new BigDecimal(value);
    }

    private int parseInteger(String value) {

        if (value == null || value.isBlank()) {
            return 0;
        }

        value = value.replace(",", "").trim();

        return Integer.parseInt(value);
    }

    private LocalDate parseDate(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return LocalDate.parse(value);
    }

    private int calculateNumberOfMonths(LocalDate dueDate) {

        if (dueDate == null || dueDate.isAfter(LocalDate.now())) {
            return 0;
        }

        LocalDate today = LocalDate.now();

        int months = Period.between(dueDate, today).getYears() * 12
                + Period.between(dueDate, today).getMonths();

        if (today.getDayOfMonth() > dueDate.getDayOfMonth()) {months++;}

        return months;
    }
}