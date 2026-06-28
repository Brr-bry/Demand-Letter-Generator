package com.karesh.demandlettergenerator;

import com.karesh.demandlettergenerator.generator.WordGenerator;
import com.karesh.demandlettergenerator.model.Customer;
import com.karesh.demandlettergenerator.parser.ExcelParser;
import com.karesh.demandlettergenerator.util.FileNameUtils;
import com.karesh.demandlettergenerator.util.OutputManager;

import java.io.InputStream;
import java.nio.file.Path;
import java.sql.SQLOutput;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        InputStream template =
                Main.class.getResourceAsStream(
                        "/templates/demandtemplate.docx");

        if (template == null) {
            throw new RuntimeException("Template not found.");
        }

        ExcelParser parser = new ExcelParser();

        List<Customer> customers =
                parser.parse(Path.of("input/raw_file.xlsx"));

        WordGenerator generator =
                new WordGenerator();

        Path batchFolder =
                OutputManager.createBatchFolder();

        int generated = 0;
        int fileNo = 1;

        for (Customer customer : customers) {

            Path output =
                    batchFolder.resolve(
                            FileNameUtils.build(customer, fileNo));

            System.out.println(output);

            generator.generate(customer, output);

            generated++;
            fileNo++;
        }

        System.out.println("Generated " + generated + " documents.");
    }
}