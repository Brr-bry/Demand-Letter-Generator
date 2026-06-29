package com.karesh.demandlettergenerator.generator.legacy;

import com.karesh.demandlettergenerator.model.Customer;
import com.karesh.demandlettergenerator.util.MoneyFormatter;
import com.karesh.demandlettergenerator.util.StringUtils;
import com.karesh.demandlettergenerator.util.NumberToWords;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class WordGeneratorPoiTl {

    public void generate(Customer customer, String outputFile) throws IOException {

        System.out.println(
                getClass().getResource("/templates/demandtemplate.docx")
        );

        InputStream inputStream =
                getClass().getResourceAsStream("/templates/demandtemplate.docx");

        if (inputStream == null) {
            throw new RuntimeException("template.docx not found.");
        }

        XWPFDocument document = new XWPFDocument(inputStream);

        replaceDocument(document, customer);

        try (FileOutputStream output = new FileOutputStream(outputFile)) {

            document.write(output);

        }

        document.close();
    }

    private void replaceDocument(XWPFDocument document,
                                 Customer customer) {

        // Paragraphs outside tables
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replaceParagraph(paragraph, customer);
        }

        // Paragraphs inside tables
        for (XWPFTable table : document.getTables()) {

            for (XWPFTableRow row : table.getRows()) {

                for (XWPFTableCell cell : row.getTableCells()) {

                    for (XWPFParagraph paragraph : cell.getParagraphs()) {

                        replaceParagraph(paragraph, customer);

                    }

                }

            }

        }

    }

    private void replaceParagraph(XWPFParagraph paragraph,
                                  Customer customer) {

        if (paragraph.getRuns().isEmpty()) {
            return;
        }

        String text = paragraph.getText();

        if (text == null || text.isBlank()) {
            return;
        }

        String original = text;

        String currentMonthYear = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH))
                .toUpperCase();

        text = text.replace("${CUR_MONTH_YEAR}", currentMonthYear);

        text = text.replace("${FULL_NAME}",
                StringUtils.toTitleCase(customer.getFullName()));

        text = text.replace("${LAST_NAME}",
                StringUtils.toTitleCase(customer.getFullName()));

        text = text.replace("${ADDRESS}",
                StringUtils.toTitleCase(customer.getAddress()));

        text = text.replace("${PHONE}",
                customer.getPhone());

        text = text.replace("${TOTAL_GROSS}",
                MoneyFormatter.format(customer.getTotalGross()));

        text = text.replace("${HIGHEST_NUM_MONTHS}",
                String.valueOf(customer.getHighestNumberOfMonths()));

        text = text.replace("${TOTAL_WITH_PENALTIES}",
                MoneyFormatter.format(customer.getTotalIncludingPenalty()));

        text = text.replace("${TOTAL_IN_WORDS}",
                NumberToWords.convert(customer.getTotalIncludingPenalty()).toUpperCase());

        // Nothing changed
        if (original.equals(text)) {
            return;
        }

        XWPFRun firstRun = paragraph.getRuns().get(0);

        boolean bold = firstRun.isBold();
        boolean italic = firstRun.isItalic();
        UnderlinePatterns underline = firstRun.getUnderline();
        int fontSize = firstRun.getFontSize();
        String fontFamily = firstRun.getFontFamily();

        while (!paragraph.getRuns().isEmpty()) {
            paragraph.removeRun(0);
        }

        XWPFRun run = paragraph.createRun();

        run.setBold(bold);
        run.setItalic(italic);
        run.setUnderline(underline);

        if (fontFamily != null)
            run.setFontFamily(fontFamily);

        if (fontSize > 0)
            run.setFontSize(fontSize);

        run.setText(text);
    }

}