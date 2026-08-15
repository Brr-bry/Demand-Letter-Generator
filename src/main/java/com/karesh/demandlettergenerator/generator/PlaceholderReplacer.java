package com.karesh.demandlettergenerator.generator;

import com.karesh.demandlettergenerator.model.Customer;
import com.karesh.demandlettergenerator.model.Transaction;
import com.karesh.demandlettergenerator.util.MoneyFormatter;
import com.karesh.demandlettergenerator.util.NumberToWords;
import com.karesh.demandlettergenerator.util.StringUtils;
import jakarta.xml.bind.JAXBElement;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PlaceholderReplacer {

    // The three header strings that identify the transaction table
    private static final String HEADER_SO_TRA  = "TRA/S.O";
    private static final String HEADER_TOTAL   = "TOTAL";
    private static final String HEADER_DUE_DATE = "DUE DATE";

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH);

    // -----------------------------------------------------------------------
    // Public entry point
    // -----------------------------------------------------------------------

    public void replace(WordprocessingMLPackage document, Customer customer) {

        Map<String, String> placeholders = buildPlaceholders(customer);

        // 1. Replace all simple {{...}} placeholders throughout the document
        replaceRecursive(document.getMainDocumentPart(), placeholders);

        // 2. Find the transaction table and inject rows
        injectTransactionRows(document, customer.getTransactions());
    }

    // -----------------------------------------------------------------------
    // Step 1 – placeholder replacement (unchanged logic, unchanged behaviour)
    // -----------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private void replaceRecursive(Object object, Map<String, String> placeholders) {

        if (object instanceof JAXBElement<?>) {
            object = ((JAXBElement<Object>) object).getValue();
        }

        if (object instanceof ContentAccessor accessor) {
            for (Object child : accessor.getContent()) {
                replaceRecursive(child, placeholders);
            }
            return;
        }

        if (object instanceof Text text) {

            System.out.println("TEXT = [" + text.getValue() + "]");

            String original = text.getValue();
            String value    = original;

            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                value = value.replace(entry.getKey(), entry.getValue());
            }

            if (!original.equals(value)) {
                System.out.println("REPLACED:");
                System.out.println("FROM: " + original);
                System.out.println("TO  : " + value);
            }

            text.setValue(value);
        }
    }

    // -----------------------------------------------------------------------
    // Step 2 – transaction table injection
    // -----------------------------------------------------------------------

    /**
     * Walks every top-level block element in the main document part, looking
     * for a {@link Tbl} whose first row contains cells with the three expected
     * header texts.  When found, appends one {@link Tr} per transaction.
     */
    private void injectTransactionRows(WordprocessingMLPackage document,
                                       List<Transaction> transactions) {

        if (transactions == null || transactions.isEmpty()) {
            return;
        }

        // The main document body
        Body body = document.getMainDocumentPart().getJaxbElement().getBody();

        for (Object block : body.getContent()) {

            Object unwrapped = unwrap(block);

            if (unwrapped instanceof Tbl table) {

                if (isTransactionTable(table)) {
                    appendTransactionRows(table, transactions);
                    return; // done – only one such table expected
                }
            }
        }

        System.out.println("WARNING: Transaction table not found in document.");
    }

    /**
     * Returns {@code true} when the first row of {@code table} contains cells
     * whose text includes all three required header strings (order-independent,
     * case-sensitive).
     */
    private boolean isTransactionTable(Tbl table) {

        List<Object> rows = table.getContent();
        if (rows.isEmpty()) return false;

        Object firstRow = unwrap(rows.get(0));
        if (!(firstRow instanceof Tr headerRow)) return false;

        // Collect all text from every cell in the first row
        List<String> cellTexts = extractCellTexts(headerRow);

        boolean hasSoTra   = cellTexts.stream().anyMatch(t -> t.contains(HEADER_SO_TRA));
        boolean hasTotal   = cellTexts.stream().anyMatch(t -> t.contains(HEADER_TOTAL));
        boolean hasDueDate = cellTexts.stream().anyMatch(t -> t.contains(HEADER_DUE_DATE));

        return hasSoTra && hasTotal && hasDueDate;
    }

    /**
     * Builds and appends one {@link Tr} per transaction to the given table.
     * The row mirrors the three-column structure identified by the headers:
     *   Col 0 – S.O # / TRA value   (soNumber / traNumber)
     *   Col 1 – TOTAL                (totalDue formatted as money)
     *   Col 2 – DUE DATE             (dueDate formatted as MM/dd/yyyy)
     *
     * Column count and cell widths are copied from the header row so the new
     * rows align perfectly with the existing table layout.
     */
    private void appendTransactionRows(Tbl table, List<Transaction> transactions) {

        // Grab the header row so we can copy its cell-width properties
        Tr headerRow = (Tr) unwrap(table.getContent().get(0));
        List<TcPr> headerCellProps = extractCellProperties(headerRow);

        ObjectFactory wmlObjectFactory = new ObjectFactory();

        for (Transaction tx : transactions) {

            Tr dataRow = wmlObjectFactory.createTr();

            // Row height: 0.16 in × 1440 twips/in = 230 twips, exact (not auto)
            TrPr trPr = wmlObjectFactory.createTrPr();
            CTHeight height = wmlObjectFactory.createCTHeight();
            height.setVal(java.math.BigInteger.valueOf(432));
            height.setHRule(STHeightRule.EXACT);
            JAXBElement<CTHeight> jaxbElement = Context.getWmlObjectFactory().createCTTrPrBaseTrHeight(height);
            trPr.getCnfStyleOrDivIdOrGridBefore().add(jaxbElement);
            dataRow.setTrPr(trPr);

            // --- Column 0: S.O # / TRA ---
            String soTra = buildSoTraText(tx);
            dataRow.getContent().add(
                    buildCell(wmlObjectFactory, soTra,
                            safeGet(headerCellProps, 0)));



            // --- Column 1: DUE DATE ---
            String dueDate = tx.getDueDate() != null
                    ? tx.getDueDate().format(DATE_FMT)
                    : "";
            dataRow.getContent().add(
                    buildCell(wmlObjectFactory, dueDate,
                            safeGet(headerCellProps, 2)));

            // --- Column 2: TOTAL ---
            String total = MoneyFormatter.format(tx.getTotalDue());
            dataRow.getContent().add(
                    buildCell(wmlObjectFactory, total,
                            safeGet(headerCellProps, 1)));

            table.getContent().add(dataRow);

            System.out.println("INJECTED ROW: " + soTra + " | " + dueDate + " | " + total );
        }
    }

    // -----------------------------------------------------------------------
    // Helper – cell / row utilities
    // -----------------------------------------------------------------------

    /**
     * Returns the concatenated text content of each cell in {@code row},
     * one entry per cell.
     */
    private List<String> extractCellTexts(Tr row) {

        List<String> texts = new ArrayList<>();

        for (Object item : row.getContent()) {
            Object cell = unwrap(item);
            if (cell instanceof Tc tc) {
                texts.add(extractText(tc));
            }
        }

        return texts;
    }

    /**
     * Recursively collects all {@link Text} values under {@code node} and
     * returns them joined as a single string.
     */
    @SuppressWarnings("unchecked")
    private String extractText(Object node) {

        if (node instanceof JAXBElement<?>) {
            node = ((JAXBElement<Object>) node).getValue();
        }

        if (node instanceof Text text) {
            return text.getValue() == null ? "" : text.getValue();
        }

        if (node instanceof ContentAccessor accessor) {
            StringBuilder sb = new StringBuilder();
            for (Object child : accessor.getContent()) {
                sb.append(extractText(child));
            }
            return sb.toString();
        }

        return "";
    }

    /**
     * Extracts the {@link TcPr} (cell properties) from each cell in
     * {@code headerRow}, preserving index position (null if absent).
     */
    private List<TcPr> extractCellProperties(Tr headerRow) {

        List<TcPr> props = new ArrayList<>();

        for (Object item : headerRow.getContent()) {
            Object cell = unwrap(item);
            if (cell instanceof Tc tc) {
                props.add(tc.getTcPr()); // may be null
            }
        }

        return props;
    }

    /**
     * Builds a single {@link Tc} containing a plain-text paragraph styled with:
     * <ul>
     *   <li>Calibri font</li>
     *   <li>Black, ½ pt (4 eighth-points) borders on all four sides</li>
     *   <li>Text horizontally centered (paragraph jc) and vertically centered (cell vAlign)</li>
     * </ul>
     * Column width is copied from {@code sourcePr} when available so the new
     * rows align with the header columns.
     */
    private Tc buildCell(ObjectFactory factory, String text, TcPr sourcePr) {

        Tc cell = factory.createTc();

        // --- Cell properties ---
        TcPr tcPr = factory.createTcPr();

        // 1. Copy column width from the header row
        if (sourcePr != null && sourcePr.getTcW() != null) {
            TblWidth w = factory.createTblWidth();
            w.setW(sourcePr.getTcW().getW());
            w.setType(sourcePr.getTcW().getType());
            tcPr.setTcW(w);
        }

        // 2. Black borders, ½ pt = 4 in OOXML eighth-of-a-point units
        CTBorder border = buildBlackBorder(factory);
        TcPrInner.TcBorders borders = factory.createTcPrInnerTcBorders();
        borders.setTop(border);
        borders.setBottom(buildBlackBorder(factory));
        borders.setLeft(buildBlackBorder(factory));
        borders.setRight(buildBlackBorder(factory));
        tcPr.setTcBorders(borders);

        // 3. Vertical center alignment
        CTVerticalJc vAlign = factory.createCTVerticalJc();
        vAlign.setVal(STVerticalJc.CENTER);
        tcPr.setVAlign(vAlign);

        cell.setTcPr(tcPr);

        // --- Paragraph (horizontal center) ---
        P paragraph = factory.createP();

        PPr pPr = factory.createPPr();
        Jc jc   = factory.createJc();
        jc.setVal(JcEnumeration.CENTER);
        pPr.setJc(jc);
        paragraph.setPPr(pPr);

        // --- Run (Book Antiqua font) ---
        R run = factory.createR();

        RPr rPr  = factory.createRPr();
        RFonts fonts = factory.createRFonts();
        fonts.setAscii("Book Antiqua");
        fonts.setHAnsi("Book Antiqua");
        fonts.setCs("Book Antiqua");
        rPr.setRFonts(fonts);
        run.setRPr(rPr);

        U underline = factory.createU();
        underline.setVal(UnderlineEnumeration.SINGLE);
        rPr.setU(underline);

        int fontSize = 12;

        HpsMeasure fontPt = factory.createHpsMeasure();
        fontPt.setVal(BigInteger.valueOf(fontSize * 2));
        rPr.setSz(fontPt);         // Sets size for regular/latin text
        rPr.setSzCs(fontPt);       // Sets size for complex scripts (optional)
        run.setRPr(rPr);

        // --- Text ---
        Text t = factory.createText();
        t.setValue(text);
        t.setSpace("preserve");

        run.getContent().add(t);
        paragraph.getContent().add(run);
        cell.getContent().add(paragraph);

        return cell;
    }

    /**
     * Creates a single black border at ½ pt (4 eighth-of-a-point units, the
     * smallest non-zero border OOXML supports) using the SINGLE line style.
     */
    private CTBorder buildBlackBorder(ObjectFactory factory) {
        CTBorder b = factory.createCTBorder();
        b.setVal(STBorder.SINGLE);
        b.setSz(java.math.BigInteger.valueOf(4));   // 4 × ⅛ pt = ½ pt
        b.setColor("FFFFFF");                       // pure black
        b.setSpace(java.math.BigInteger.ZERO);
        return b;
    }

    /**
     * Formats the S.O # / TRA column value.
     * Combines soNumber and traNumber with a " / " separator when both are
     * present; falls back gracefully when either is missing.
     */
    private String buildSoTraText(Transaction tx) {

        String so  = tx.getSoNumber()  != null ? tx.getSoNumber().trim()  : "";
        String tra = tx.getTraNumber() != null ? tx.getTraNumber().trim() : "";

        if (!so.isEmpty() && !tra.isEmpty()) return tra + " / " + so;
        if (!so.isEmpty())  return so;
        if (!tra.isEmpty()) return tra;
        return "";
    }

    /** Null-safe list accessor. */
    private <T> T safeGet(List<T> list, int index) {
        return (index >= 0 && index < list.size()) ? list.get(index) : null;
    }

    /** Unwraps a {@link JAXBElement} if necessary, otherwise returns as-is. */
    @SuppressWarnings("unchecked")
    private Object unwrap(Object o) {
        return (o instanceof JAXBElement<?>) ? ((JAXBElement<Object>) o).getValue() : o;
    }

    // -----------------------------------------------------------------------
    // Placeholder map builder (unchanged)
    // -----------------------------------------------------------------------

    private Map<String, String> buildPlaceholders(Customer customer) {

        Map<String, String> map = new HashMap<>();

        String monthYear =
                LocalDate.now()
                        .format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH))
                        .toUpperCase();

        String currDate =
                LocalDate.now()
                        .format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH))
                        .toUpperCase();

        String traDate =customer.getOldestTransactionDueDate()
                        .format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH))
                        .toUpperCase();

        map.put("{{CUR_MONTH_YEAR}}", monthYear);
        map.put("{{CUR_DATE}}", currDate);
        map.put("{{FULL_NAME}}",             StringUtils.toTitleCase(customer.getFullName()));
        map.put("{{LAST_NAME}}",             StringUtils.toTitleCase(customer.getLastName()));
        map.put("{{ADDRESS}}",               StringUtils.toTitleCase(customer.getAddress()));
        map.put("{{PHONE}}",                 customer.getPhone());
        map.put("{{TRA_DATE}}",              traDate);
        map.put("{{TOTAL_GROSS}}",           MoneyFormatter.format(customer.getTotalGross()));
        map.put("{{TOTAL_WITH_PENALTIES}}",  MoneyFormatter.format(customer.getTotalIncludingPenalty()));
        map.put("{{TOTAL_IN_WORDS}}",        NumberToWords.convert(customer.getTotalIncludingPenalty()));
        map.put("{{HIGHEST_NUM_MONTHS}}",    String.valueOf(customer.getHighestNumberOfMonths()));

        return map;
    }
}