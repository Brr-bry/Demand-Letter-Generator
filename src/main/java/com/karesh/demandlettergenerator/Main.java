package com.karesh.demandlettergenerator;

import com.karesh.demandlettergenerator.generator.DocxInspector;
import com.karesh.demandlettergenerator.generator.WordGenerator;
import com.karesh.demandlettergenerator.model.Customer;
import com.karesh.demandlettergenerator.parser.ExcelParser;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.InputStream;
import java.nio.file.Path;
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

        generator.generate(
                customers.get(1),
                Path.of("output/output.docx"));

    }

}