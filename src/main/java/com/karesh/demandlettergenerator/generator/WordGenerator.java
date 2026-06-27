package com.karesh.demandlettergenerator.generator;

import com.deepoove.poi.XWPFTemplate;
import com.karesh.demandlettergenerator.model.Customer;
import com.karesh.demandlettergenerator.util.MoneyFormatter;
import com.karesh.demandlettergenerator.util.NumberToWords;
import com.karesh.demandlettergenerator.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WordGenerator {

    public void generate(Customer customer, Path outputFile) throws IOException {

        InputStream template =
                getClass().getResourceAsStream("/templates/demandtemplate.docx");

        if (template == null) {
            throw new RuntimeException("Template not found.");
        }

        Map<String, Object> data = buildData(customer);

        XWPFTemplate.compile(template)
                .render(data)
                .writeToFile(outputFile.toString());
    }

    private Map<String, Object> buildData(Customer customer) {

        Map<String, Object> map = new HashMap<>();

        String monthYear =
                LocalDate.now()
                        .format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH))
                        .toUpperCase();

        map.put("CUR_MONTH_YEAR", monthYear);

        map.put("FULL_NAME",
                StringUtils.toTitleCase(customer.getFullName()));

        map.put("LAST_NAME",
                StringUtils.toTitleCase(customer.getLastName()));

        map.put("ADDRESS",
                StringUtils.toTitleCase(customer.getAddress()));

        map.put("PHONE",
                customer.getPhone());

        map.put("TOTAL_GROSS",
                MoneyFormatter.format(customer.getTotalGross()));

        map.put("HIGHEST_NUM_MONTHS",
                customer.getHighestNumberOfMonths());

        map.put("TOTAL_WITH_PENALTIES",
                MoneyFormatter.format(customer.getTotalIncludingPenalty()));

        map.put("TOTAL_IN_WORDS",
                NumberToWords.convert(customer.getTotalIncludingPenalty()));

        return map;
    }

}