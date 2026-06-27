package com.karesh.demandlettergenerator;

import com.karesh.demandlettergenerator.generator.legacy.WordGeneratorPoiTl;
import com.karesh.demandlettergenerator.model.Customer;
import com.karesh.demandlettergenerator.parser.ExcelParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        ExcelParser parser = new ExcelParser();

        try {

            List<Customer> customers = parser.parse(Path.of("input/raw_file.xlsx"));

            WordGeneratorPoiTl generator = new WordGeneratorPoiTl();

            generator.generate(

                    customers.get(1),

                    "output/Test.docx"

            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}