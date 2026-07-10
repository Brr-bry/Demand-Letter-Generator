package com.karesh.demandlettergenerator.generator;

import com.karesh.demandlettergenerator.model.Customer;

import com.karesh.demandlettergenerator.util.TemplateManager;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WordGenerator {

    private final PlaceholderReplacer placeholderReplacer =
            new PlaceholderReplacer();



    public void generate(Customer customer,
                         Path outputFile) throws Exception {

        Path template = TemplateManager.getUserTemplate();

        WordprocessingMLPackage document =
                WordprocessingMLPackage.load(
                        Files.newInputStream(template)
                );

        placeholderReplacer.replace(document, customer);

        document.save(outputFile.toFile());
    }

}