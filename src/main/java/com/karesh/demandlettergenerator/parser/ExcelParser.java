package com.karesh.demandlettergenerator.parser;

import com.karesh.demandlettergenerator.model.Customer;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ExcelParser {
    private final DataFormatter formatter = new DataFormatter();

    public List<Customer> parse(Path excelFile) throws IOException {

        List<Customer> customers = new ArrayList<>();

        try (InputStream inputStream = Files.newInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            System.out.println("Workbook loaded.");
            System.out.println("Sheet: " + sheet.getSheetName());

            for (Row row : sheet) {

                StringBuilder rowText = new StringBuilder();

                boolean hasValue = false;

                for (int i = 0; i < row.getLastCellNum(); i++) {

                    String value = getCellValue(row, i);

                    if (!value.isBlank()) {
                        hasValue = true;
                    }

                    rowText.append(value).append(" | ");
                }

                if (hasValue) {
                    System.out.println(row.getRowNum() + " -> " + rowText);
                }

            }

        }

        return customers;
    }

    private String getCellValue(Row row, int columnIndex) {

        if (row == null) {
            return "";
        }

        Cell cell = row.getCell(columnIndex);

        if (cell == null) {
            return "";
        }

        return formatter.formatCellValue(cell).trim();
    }
}