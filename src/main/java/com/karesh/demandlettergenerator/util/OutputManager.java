package com.karesh.demandlettergenerator.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class OutputManager {

    private OutputManager() {
    }

    /**
     * Documents/Demand Letters
     */
    public static Path getGeneratedRoot() {

        return Path.of(
                System.getProperty("user.home"),
                "Documents",
                "Demand Letters"
        );

    }

    /**
     * Documents/Demand Letters/2026/JUNE/Batch_xxx
     */
    public static Path createBatchFolder() throws IOException {

        LocalDate today = LocalDate.now();

        String year = String.valueOf(today.getYear());

        String month = today.getMonth().name();

        String batch =
                "Batch_" +
                        LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss"));

        Path folder =
                getGeneratedRoot()
                        .resolve(year)
                        .resolve(month)
                        .resolve(batch);

        Files.createDirectories(folder);

        return folder;

    }

}