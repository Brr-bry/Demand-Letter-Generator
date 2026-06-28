package com.karesh.demandlettergenerator.service;

import com.karesh.demandlettergenerator.generator.WordGenerator;
import com.karesh.demandlettergenerator.model.Customer;
import com.karesh.demandlettergenerator.parser.ExcelParser;
import com.karesh.demandlettergenerator.util.FileNameUtils;
import com.karesh.demandlettergenerator.util.OutputManager;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;

import java.nio.file.Path;
import java.util.List;

public class GenerationService extends Task<Path> {

    private final Path excel;


    public GenerationService(Path excel) {

        this.excel = excel;

    }

    @Override
    protected Path call() throws Exception {

        updateMessage("Reading Excel file...");

        ExcelParser parser = new ExcelParser();

        List<Customer> customers =
                parser.parse(excel);

        WordGenerator generator =
                new WordGenerator();

        Path batchFolder =
                OutputManager.createBatchFolder();

        int total = customers.size();

        for (int i = 0; i < total; i++) {

            Customer customer =
                    customers.get(i);



            updateMessage("Processing: " + customer.getLastName()
                    + " \n(" + (i + 1) + "/" + total + ")");


            updateProgress(i + 1, total);

            generator.generate(

                    customer,

                    batchFolder.resolve(

                            FileNameUtils.build(customer, i + 1)

                    )
            );

        }

        updateMessage("Finished \n("+ total + "/" + total +")");
        updateProgress(total, total);


        return batchFolder;
    }


}