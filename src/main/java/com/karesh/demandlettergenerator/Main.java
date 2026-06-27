package com.karesh.demandlettergenerator;

import com.karesh.demandlettergenerator.parser.ExcelParser;

import java.io.IOException;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {

        ExcelParser parser = new ExcelParser();

        try {

            parser.parse(Path.of("input/raw_file.xlsx"));

        } catch (IOException e) {

            System.out.println("Unable to read Excel file.");
            e.printStackTrace();

        }

    }
}