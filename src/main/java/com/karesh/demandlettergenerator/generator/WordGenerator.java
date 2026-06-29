package com.karesh.demandlettergenerator.generator;

import com.karesh.demandlettergenerator.model.Customer;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.InputStream;
import java.nio.file.Path;

public class WordGenerator {

    private final PlaceholderReplacer placeholderReplacer =
            new PlaceholderReplacer();

    private final TableGenerator tableGenerator =
            new TableGenerator();

    public void generate(Customer customer,
                         Path outputFile) throws Exception {

        InputStream template =
                getClass().getResourceAsStream(
                        "/templates/demandtemplate.docx");

        if (template == null) {
            throw new RuntimeException("Template not found.");
        }

        WordprocessingMLPackage document =
                WordprocessingMLPackage.load(template);

        placeholderReplacer.replace(document, customer);

        // tableGenerator.generate(document, customer);

        document.save(outputFile.toFile());
    }

}