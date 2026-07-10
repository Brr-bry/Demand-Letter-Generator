package com.karesh.demandlettergenerator.util;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

public class TemplateManager {

    private static final String TEMPLATE_NAME = "demandtemplate.docx";
    private static final String APP_FOLDER = "DemandLetterGenerator";

    private static Path getTemplateFolder() {

        String os = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");

        if (os.contains("win")) {
            return Paths.get(
                    System.getenv("LOCALAPPDATA"),
                    "DemandLetterGenerator",
                    "templates"
            );
        }

        if (os.contains("mac")) {
            return Paths.get(
                    home,
                    "Library",
                    "Application Support",
                    "DemandLetterGenerator",
                    "templates"
            );
        }

        // Linux
        return Paths.get(
                home,
                ".local",
                "share",
                "DemandLetterGenerator",
                "templates"
        );
    }

    public static Path getUserTemplate() throws IOException {

        Path folder = getTemplateFolder();
        Files.createDirectories(folder);

        Path template = folder.resolve(TEMPLATE_NAME);

        if (!Files.exists(template)) {

            try (InputStream in =
                         TemplateManager.class.getResourceAsStream(
                                 "/templates/" + TEMPLATE_NAME)) {

                if (in == null) {
                    throw new IOException("Bundled template not found.");
                }

                Files.copy(in, template);
            }
        }

        return template;
    }

    public static void openTemplate() throws Exception {

        Path template = getUserTemplate();

        Desktop.getDesktop().open(template.toFile());
    }

    public static void restoreDefaultTemplate() throws IOException {

        Path template = getUserTemplate();

        try (InputStream in =
                     TemplateManager.class.getResourceAsStream(
                             "/templates/" + TEMPLATE_NAME)) {

            if (in == null) {
                throw new IOException("Bundled template not found.");
            }

            Files.copy(
                    in,
                    template,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

}