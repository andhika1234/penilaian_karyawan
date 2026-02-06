package id.co.lua.pbj.penilaian_karyawan.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import id.co.lua.pbj.penilaian_karyawan.model.apps.Karyawan;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PdfGenerator {

    private static final Font FONT_TITLE = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font FONT_HEADER = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font FONT_NORMAL = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    private static final Font FONT_SMALL = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);

    public static ByteArrayOutputStream generateKaryawanReport(List<Karyawan> karyawanList,
                                                               String logoPath,
                                                               String companyName,
                                                               String companyAddress,
                                                               String printDate,
                                                               String directorName) throws DocumentException, IOException {

        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();

            // Add header with logo and company info
            addHeader(document, logoPath, companyName, companyAddress);

            // Add separator line
            addSeparatorLine(document);

            // Add title
            addTitle(document, "LAPORAN DATA KARYAWAN");

            // Add some space
            document.add(new Paragraph(" "));

            // Add table with employee data
            addKaryawanTable(document, karyawanList);

            // Add signature section
            addSignature(document, printDate, directorName);

            document.close();
        } catch (Exception e) {
            throw e;
        }

        return outputStream;
    }

    private static void addHeader(Document document, String logoPath, String companyName, String companyAddress)
            throws DocumentException, IOException {

        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setSpacingAfter(10f);

        try {
            headerTable.setWidths(new float[]{1, 3});
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        // Logo cell
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);

        try {
            // Try to load logo from resources
            InputStream logoStream = PdfGenerator.class.getResourceAsStream(logoPath);
            if (logoStream != null) {
                byte[] logoBytes = logoStream.readAllBytes();
                Image logo = Image.getInstance(logoBytes);
                logo.scaleToFit(80, 80);
                logoCell.addElement(logo);
            } else {
                // If logo not found, add placeholder text
                Paragraph placeholder = new Paragraph("LOGO", FONT_HEADER);
                placeholder.setAlignment(Element.ALIGN_CENTER);
                logoCell.addElement(placeholder);
            }
        } catch (Exception e) {
            // If logo loading fails, add placeholder
            Paragraph placeholder = new Paragraph("LOGO", FONT_HEADER);
            placeholder.setAlignment(Element.ALIGN_CENTER);
            logoCell.addElement(placeholder);
        }

        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerTable.addCell(logoCell);

        // Company info cell
        PdfPCell companyCell = new PdfPCell();
        companyCell.setBorder(Rectangle.NO_BORDER);

        Paragraph companyNamePara = new Paragraph(companyName, FONT_HEADER);
        companyNamePara.setAlignment(Element.ALIGN_CENTER);

        Paragraph addressPara = new Paragraph(companyAddress, FONT_NORMAL);
        addressPara.setAlignment(Element.ALIGN_CENTER);

        companyCell.addElement(companyNamePara);
        companyCell.addElement(addressPara);
        companyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerTable.addCell(companyCell);

        document.add(headerTable);
    }

    private static void addSeparatorLine(Document document) throws DocumentException {
        Paragraph separator = new Paragraph();
        separator.add(new Chunk("_____________________________________________________________________"));
        separator.setAlignment(Element.ALIGN_CENTER);
        document.add(separator);
        document.add(new Paragraph(" "));
    }

    private static void addTitle(Document document, String title) throws DocumentException {
        Paragraph titleParagraph = new Paragraph(title, FONT_TITLE);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);
        titleParagraph.setSpacingAfter(10f);
        document.add(titleParagraph);
    }

    private static void addKaryawanTable(Document document, List<Karyawan> karyawanList) throws DocumentException {
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        try {
            table.setWidths(new float[]{1, 3, 2.5f, 2, 2, 2});
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        // Add table headers
        addTableHeader(table, "No");
        addTableHeader(table, "Nama Karyawan");
        addTableHeader(table, "NIK");
        addTableHeader(table, "No. Telepon");
        addTableHeader(table, "Divisi");
        addTableHeader(table, "Jabatan");

        // Add table data
        int no = 1;
        for (Karyawan karyawan : karyawanList) {
            addTableCell(table, String.valueOf(no++), Element.ALIGN_CENTER);
            addTableCell(table, karyawan.getNamaKaryawan(), Element.ALIGN_LEFT);
            addTableCell(table, karyawan.getNik(), Element.ALIGN_LEFT);
            addTableCell(table, karyawan.getNomorTelepon(), Element.ALIGN_LEFT);
            addTableCell(table, karyawan.getDivisi() != null ? karyawan.getDivisi().getNamaDivisi() : "-", Element.ALIGN_LEFT);
            addTableCell(table, karyawan.getJabatan() != null ? karyawan.getJabatan().getNamaJabatan() : "-", Element.ALIGN_LEFT);
        }

        document.add(table);
    }

    private static void addTableHeader(PdfPTable table, String headerText) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph para = new Paragraph(headerText, FONT_HEADER);
        para.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(para);

        table.addCell(cell);
    }

    private static void addTableCell(PdfPTable table, String text, int alignment) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph para = new Paragraph(text != null ? text : "", FONT_NORMAL);
        para.setAlignment(alignment);
        cell.addElement(para);

        table.addCell(cell);
    }

    private static void addSignature(Document document, String printDate, String directorName) throws DocumentException {

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));


        PdfPTable signatureTable = new PdfPTable(2);
        signatureTable.setWidthPercentage(100);

        try {
            signatureTable.setWidths(new float[]{1, 1});
        } catch (DocumentException e) {
            e.printStackTrace();
        }


        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.addElement(new Paragraph(" "));
        signatureTable.addCell(leftCell);

        // Right cell (signature section)
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        // Date and location
        Paragraph datePara = new Paragraph("Jakarta, " + printDate, FONT_NORMAL);
        datePara.setAlignment(Element.ALIGN_CENTER);
        rightCell.addElement(datePara);

        // Add space
        rightCell.addElement(new Paragraph(" "));

        // Position title
        Paragraph positionPara = new Paragraph("Direktur", FONT_NORMAL);
        positionPara.setAlignment(Element.ALIGN_CENTER);
        rightCell.addElement(positionPara);

        // Add space for signature
        rightCell.addElement(new Paragraph(" "));
        rightCell.addElement(new Paragraph(" "));
        rightCell.addElement(new Paragraph(" "));

        // Signature line
        Paragraph signatureLine = new Paragraph("_______________________", FONT_NORMAL);
        signatureLine.setAlignment(Element.ALIGN_CENTER);
        rightCell.addElement(signatureLine);

        // Director name
        Paragraph namePara = new Paragraph(directorName, FONT_NORMAL);
        namePara.setAlignment(Element.ALIGN_CENTER);
        rightCell.addElement(namePara);

        signatureTable.addCell(rightCell);
        document.add(signatureTable);
    }
}

