package com.bookkeeping.service;

import com.bookkeeping.model.BalanceSheetData;
import com.bookkeeping.model.IncomeStatementData;
import com.bookkeeping.model.TrialBalanceData;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Service untuk menghasilkan laporan PDF
 */
public class PDFReportService {
    
    private final NumberFormat currencyFormat;
    private final DateTimeFormatter dateFormatter;
    
    public PDFReportService() {
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
        this.dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    }
    
    /**
     * Generate PDF Trial Balance
     */
    public void generateTrialBalancePDF(TrialBalanceData data, String filePath) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        
        document.open();
        
        // Header
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        
        Paragraph title = new Paragraph("TRIAL BALANCE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        
        Paragraph asOfDate = new Paragraph("As of " + data.getAsOfDate().format(dateFormatter), headerFont);
        asOfDate.setAlignment(Element.ALIGN_CENTER);
        asOfDate.setSpacingAfter(20);
        document.add(asOfDate);
        
        // Table
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{15f, 45f, 20f, 20f});
        
        // Table headers
        addTableHeader(table, "Account Code", headerFont);
        addTableHeader(table, "Account Name", headerFont);
        addTableHeader(table, "Debit", headerFont);
        addTableHeader(table, "Credit", headerFont);
        
        // Table data
        for (TrialBalanceData.TrialBalanceItem item : data.getItems()) {
            addTableCell(table, item.getAccountCode(), normalFont);
            addTableCell(table, item.getAccountName(), normalFont);
            addTableCell(table, formatCurrency(item.getDebitBalance()), normalFont);
            addTableCell(table, formatCurrency(item.getCreditBalance()), normalFont);
        }
        
        // Total row
        addTableCell(table, "", headerFont);
        addTableCell(table, "TOTAL", headerFont);
        addTableCell(table, formatCurrency(data.getTotalDebits()), headerFont);
        addTableCell(table, formatCurrency(data.getTotalCredits()), headerFont);
        
        document.add(table);
        
        // Balance validation
        Paragraph balanceInfo = new Paragraph();
        if (data.isBalanced()) {
            balanceInfo.add(new Phrase("✓ Trial Balance is BALANCED", 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.GREEN)));
        } else {
            balanceInfo.add(new Phrase("✗ Trial Balance is NOT BALANCED", 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.RED)));
        }
        balanceInfo.setSpacingBefore(20);
        document.add(balanceInfo);
        
        document.close();
    }
    
    /**
     * Generate PDF Balance Sheet
     */
    public void generateBalanceSheetPDF(BalanceSheetData data, String filePath) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        
        document.open();
        
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
        Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        
        // Header
        Paragraph title = new Paragraph("BALANCE SHEET", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        
        Paragraph asOfDate = new Paragraph("As of " + data.getAsOfDate().format(dateFormatter), headerFont);
        asOfDate.setAlignment(Element.ALIGN_CENTER);
        asOfDate.setSpacingAfter(20);
        document.add(asOfDate);
        
        // Assets Section
        Paragraph assetsHeader = new Paragraph("ASSETS", subHeaderFont);
        assetsHeader.setSpacingBefore(10);
        document.add(assetsHeader);
        
        PdfPTable assetsTable = new PdfPTable(3);
        assetsTable.setWidthPercentage(100);
        assetsTable.setWidths(new float[]{15f, 60f, 25f});
        
        for (BalanceSheetData.BalanceSheetItem item : data.getAssets()) {
            addTableCell(assetsTable, item.getAccountCode(), normalFont);
            addTableCell(assetsTable, item.getAccountName(), normalFont);
            addTableCell(assetsTable, formatCurrency(item.getAmount()), normalFont);
        }
        
        // Total Assets
        addTableCell(assetsTable, "", subHeaderFont);
        addTableCell(assetsTable, "TOTAL ASSETS", subHeaderFont);
        addTableCell(assetsTable, formatCurrency(data.getTotalAssets()), subHeaderFont);
        
        document.add(assetsTable);
        
        // Liabilities Section
        Paragraph liabilitiesHeader = new Paragraph("LIABILITIES", subHeaderFont);
        liabilitiesHeader.setSpacingBefore(20);
        document.add(liabilitiesHeader);
        
        PdfPTable liabilitiesTable = new PdfPTable(3);
        liabilitiesTable.setWidthPercentage(100);
        liabilitiesTable.setWidths(new float[]{15f, 60f, 25f});
        
        for (BalanceSheetData.BalanceSheetItem item : data.getLiabilities()) {
            addTableCell(liabilitiesTable, item.getAccountCode(), normalFont);
            addTableCell(liabilitiesTable, item.getAccountName(), normalFont);
            addTableCell(liabilitiesTable, formatCurrency(item.getAmount()), normalFont);
        }
        
        // Total Liabilities
        addTableCell(liabilitiesTable, "", subHeaderFont);
        addTableCell(liabilitiesTable, "TOTAL LIABILITIES", subHeaderFont);
        addTableCell(liabilitiesTable, formatCurrency(data.getTotalLiabilities()), subHeaderFont);
        
        document.add(liabilitiesTable);
        
        // Equity Section
        Paragraph equityHeader = new Paragraph("EQUITY", subHeaderFont);
        equityHeader.setSpacingBefore(20);
        document.add(equityHeader);
        
        PdfPTable equityTable = new PdfPTable(3);
        equityTable.setWidthPercentage(100);
        equityTable.setWidths(new float[]{15f, 60f, 25f});
        
        for (BalanceSheetData.BalanceSheetItem item : data.getEquity()) {
            addTableCell(equityTable, item.getAccountCode(), normalFont);
            addTableCell(equityTable, item.getAccountName(), normalFont);
            addTableCell(equityTable, formatCurrency(item.getAmount()), normalFont);
        }
        
        // Total Equity
        addTableCell(equityTable, "", subHeaderFont);
        addTableCell(equityTable, "TOTAL EQUITY", subHeaderFont);
        addTableCell(equityTable, formatCurrency(data.getTotalEquity()), subHeaderFont);
        
        document.add(equityTable);
        
        // Total Liabilities + Equity
        PdfPTable totalTable = new PdfPTable(3);
        totalTable.setWidthPercentage(100);
        totalTable.setWidths(new float[]{15f, 60f, 25f});
        totalTable.setSpacingBefore(10);
        
        addTableCell(totalTable, "", headerFont);
        addTableCell(totalTable, "TOTAL LIABILITIES + EQUITY", headerFont);
        addTableCell(totalTable, formatCurrency(data.getTotalLiabilitiesAndEquity()), headerFont);
        
        document.add(totalTable);
        
        document.close();
    }
    
    /**
     * Generate PDF Income Statement
     */
    public void generateIncomeStatementPDF(IncomeStatementData data, String filePath) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        
        document.open();
        
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
        Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        
        // Header
        Paragraph title = new Paragraph("INCOME STATEMENT", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        
        Paragraph period = new Paragraph(data.getPeriodDisplay(), headerFont);
        period.setAlignment(Element.ALIGN_CENTER);
        period.setSpacingAfter(20);
        document.add(period);
        
        // Revenue Section
        Paragraph revenueHeader = new Paragraph("REVENUE", subHeaderFont);
        document.add(revenueHeader);
        
        PdfPTable revenueTable = new PdfPTable(3);
        revenueTable.setWidthPercentage(100);
        revenueTable.setWidths(new float[]{15f, 60f, 25f});
        
        for (IncomeStatementData.IncomeStatementItem item : data.getRevenues()) {
            addTableCell(revenueTable, item.getAccountCode(), normalFont);
            addTableCell(revenueTable, item.getAccountName(), normalFont);
            addTableCell(revenueTable, formatCurrency(item.getAmount()), normalFont);
        }
        
        addTableCell(revenueTable, "", subHeaderFont);
        addTableCell(revenueTable, "TOTAL REVENUE", subHeaderFont);
        addTableCell(revenueTable, formatCurrency(data.getTotalRevenue()), subHeaderFont);
        
        document.add(revenueTable);
        
        // Cost of Goods Sold Section
        if (!data.getCostOfGoodsSold().isEmpty()) {
            Paragraph cogsHeader = new Paragraph("COST OF GOODS SOLD", subHeaderFont);
            cogsHeader.setSpacingBefore(15);
            document.add(cogsHeader);
            
            PdfPTable cogsTable = new PdfPTable(3);
            cogsTable.setWidthPercentage(100);
            cogsTable.setWidths(new float[]{15f, 60f, 25f});
            
            for (IncomeStatementData.IncomeStatementItem item : data.getCostOfGoodsSold()) {
                addTableCell(cogsTable, item.getAccountCode(), normalFont);
                addTableCell(cogsTable, item.getAccountName(), normalFont);
                addTableCell(cogsTable, formatCurrency(item.getAmount()), normalFont);
            }
            
            addTableCell(cogsTable, "", subHeaderFont);
            addTableCell(cogsTable, "TOTAL COST OF GOODS SOLD", subHeaderFont);
            addTableCell(cogsTable, formatCurrency(data.getTotalCostOfGoodsSold()), subHeaderFont);
            
            document.add(cogsTable);
            
            // Gross Profit
            PdfPTable grossProfitTable = new PdfPTable(3);
            grossProfitTable.setWidthPercentage(100);
            grossProfitTable.setWidths(new float[]{15f, 60f, 25f});
            grossProfitTable.setSpacingBefore(10);
            
            addTableCell(grossProfitTable, "", headerFont);
            addTableCell(grossProfitTable, "GROSS PROFIT", headerFont);
            addTableCell(grossProfitTable, formatCurrency(data.getGrossProfit()), headerFont);
            
            document.add(grossProfitTable);
        }
        
        // Expenses Section
        Paragraph expensesHeader = new Paragraph("EXPENSES", subHeaderFont);
        expensesHeader.setSpacingBefore(15);
        document.add(expensesHeader);
        
        PdfPTable expensesTable = new PdfPTable(3);
        expensesTable.setWidthPercentage(100);
        expensesTable.setWidths(new float[]{15f, 60f, 25f});
        
        for (IncomeStatementData.IncomeStatementItem item : data.getExpenses()) {
            addTableCell(expensesTable, item.getAccountCode(), normalFont);
            addTableCell(expensesTable, item.getAccountName(), normalFont);
            addTableCell(expensesTable, formatCurrency(item.getAmount()), normalFont);
        }
        
        addTableCell(expensesTable, "", subHeaderFont);
        addTableCell(expensesTable, "TOTAL EXPENSES", subHeaderFont);
        addTableCell(expensesTable, formatCurrency(data.getTotalExpenses()), subHeaderFont);
        
        document.add(expensesTable);
        
        // Net Income
        PdfPTable netIncomeTable = new PdfPTable(3);
        netIncomeTable.setWidthPercentage(100);
        netIncomeTable.setWidths(new float[]{15f, 60f, 25f});
        netIncomeTable.setSpacingBefore(15);
        
        addTableCell(netIncomeTable, "", headerFont);
        addTableCell(netIncomeTable, "NET INCOME", headerFont);
        
        BaseColor netIncomeColor = data.getNetIncome().compareTo(java.math.BigDecimal.ZERO) >= 0 ? 
            BaseColor.GREEN : BaseColor.RED;
        Font netIncomeFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, netIncomeColor);
        
        PdfPCell netIncomeCell = new PdfPCell(new Phrase(formatCurrency(data.getNetIncome()), netIncomeFont));
        netIncomeCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        netIncomeCell.setPadding(5);
        netIncomeTable.addCell(netIncomeCell);
        
        document.add(netIncomeTable);
        
        document.close();
    }
    
    /**
     * Add table header cell
     */
    private void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell header = new PdfPCell(new Phrase(text, font));
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setPadding(5);
        table.addCell(header);
    }
    
    /**
     * Add table cell
     */
    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(text.matches("\\d.*") ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT);
        cell.setPadding(5);
        table.addCell(cell);
    }
    
    /**
     * Format currency
     */
    private String formatCurrency(java.math.BigDecimal amount) {
        if (amount == null || amount.compareTo(java.math.BigDecimal.ZERO) == 0) {
            return "-";
        }
        return currencyFormat.format(amount);
    }
}
