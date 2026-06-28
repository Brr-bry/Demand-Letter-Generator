package com.karesh.demandlettergenerator.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OutputManager {

    public static Path createBatchFolder() throws IOException {

        LocalDateTime now = LocalDateTime.now();

        String year =
                now.format(DateTimeFormatter.ofPattern("yyyy"));

        String month =
                now.format(DateTimeFormatter.ofPattern("MM-MMMM"));

        String batch =
                "Batch-" +
                        now.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));

        Path folder = Path.of(
                "generated",
                year,
                month,
                batch
        );

        Files.createDirectories(folder);

        return folder;
    }

}