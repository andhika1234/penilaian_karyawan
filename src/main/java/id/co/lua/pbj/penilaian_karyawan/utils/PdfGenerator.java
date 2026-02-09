package id.co.lua.pbj.penilaian_karyawan.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import id.co.lua.pbj.penilaian_karyawan.model.apps.Karyawan;
import id.co.lua.pbj.penilaian_karyawan.model.apps.Normalisasi;
import id.co.lua.pbj.penilaian_karyawan.model.apps.PenilaianKaryawan;
import id.co.lua.pbj.penilaian_karyawan.model.dto.NilaiReferensiDTO;

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

    public static ByteArrayOutputStream generateDivisiReport(List<id.co.lua.pbj.penilaian_karyawan.model.apps.Divisi> divisiList,
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
            addTitle(document, "LAPORAN DATA DIVISI");

            // Add some space
            document.add(new Paragraph(" "));

            // Add table with division data
            addDivisiTable(document, divisiList);

            // Add signature section
            addSignature(document, printDate, directorName);

            document.close();
        } catch (Exception e) {
            throw e;
        }

        return outputStream;
    }

    private static void addDivisiTable(Document document, List<id.co.lua.pbj.penilaian_karyawan.model.apps.Divisi> divisiList) throws DocumentException {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        try {
            table.setWidths(new float[]{1, 4, 2});
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        // Add table headers
        addTableHeader(table, "No");
        addTableHeader(table, "Nama Divisi");
        addTableHeader(table, "Status");

        // Add table data
        int no = 1;
        for (id.co.lua.pbj.penilaian_karyawan.model.apps.Divisi divisi : divisiList) {
            addTableCell(table, String.valueOf(no++), Element.ALIGN_CENTER);
            addTableCell(table, divisi.getNamaDivisi(), Element.ALIGN_LEFT);
            addTableCell(table, divisi.getStatusAktif() ? "Aktif" : "Tidak Aktif", Element.ALIGN_CENTER);
        }

        document.add(table);
    }

    public static ByteArrayOutputStream generateKriteriaReport(List<id.co.lua.pbj.penilaian_karyawan.model.apps.KriteriaPenilaian> kriteriaList,
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
            addTitle(document, "LAPORAN DATA KRITERIA PENILAIAN");

            // Add some space
            document.add(new Paragraph(" "));

            // Add table with kriteria data
            addKriteriaTable(document, kriteriaList);

            // Add signature section
            addSignature(document, printDate, directorName);

            document.close();
        } catch (Exception e) {
            throw e;
        }

        return outputStream;
    }

    private static void addKriteriaTable(Document document, List<id.co.lua.pbj.penilaian_karyawan.model.apps.KriteriaPenilaian> kriteriaList) throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        try {
            table.setWidths(new float[]{1, 2, 4, 2, 2});
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        // Add table headers
        addTableHeader(table, "No");
        addTableHeader(table, "Kode Kriteria");
        addTableHeader(table, "Nama Kriteria");
        addTableHeader(table, "Bobot (%)");
        addTableHeader(table, "Status");

        // Add table data
        int no = 1;
        for (id.co.lua.pbj.penilaian_karyawan.model.apps.KriteriaPenilaian kriteria : kriteriaList) {
            addTableCell(table, String.valueOf(no++), Element.ALIGN_CENTER);
            addTableCell(table, kriteria.getKodeKriteria(), Element.ALIGN_CENTER);
            addTableCell(table, kriteria.getNamaKriteria(), Element.ALIGN_LEFT);
            addTableCell(table, String.valueOf(kriteria.getBobot()), Element.ALIGN_CENTER);
            addTableCell(table, kriteria.getStatusAktif() ? "Aktif" : "Tidak Aktif", Element.ALIGN_CENTER);
        }

        document.add(table);
    }

    public static ByteArrayOutputStream generateJabatanReport(List<id.co.lua.pbj.penilaian_karyawan.model.apps.Jabatan> jabatanList,
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
            addTitle(document, "LAPORAN DATA JABATAN");

            // Add some space
            document.add(new Paragraph(" "));

            // Add table with jabatan data
            addJabatanTable(document, jabatanList);

            // Add signature section
            addSignature(document, printDate, directorName);

            document.close();
        } catch (Exception e) {
            throw e;
        }

        return outputStream;
    }

    private static void addJabatanTable(Document document, List<id.co.lua.pbj.penilaian_karyawan.model.apps.Jabatan> jabatanList) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        try {
            table.setWidths(new float[]{1, 2, 4, 2});
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        // Add table headers
        addTableHeader(table, "No");
        addTableHeader(table, "Kode Jabatan");
        addTableHeader(table, "Nama Jabatan");
        addTableHeader(table, "Status");

        // Add table data
        int no = 1;
        for (id.co.lua.pbj.penilaian_karyawan.model.apps.Jabatan jabatan : jabatanList) {
            addTableCell(table, String.valueOf(no++), Element.ALIGN_CENTER);
            addTableCell(table, jabatan.getKodeJabatan(), Element.ALIGN_CENTER);
            addTableCell(table, jabatan.getNamaJabatan(), Element.ALIGN_LEFT);
            addTableCell(table, jabatan.getStatusAktif() ? "Aktif" : "Tidak Aktif", Element.ALIGN_CENTER);
        }

        document.add(table);
    }

    public static ByteArrayOutputStream generatePenilaianKaryawanReport(List<id.co.lua.pbj.penilaian_karyawan.model.apps.PenilaianKaryawan> penilaianList,
                                                                        String logoPath,
                                                                        String companyName,
                                                                        String companyAddress,
                                                                        String printDate,
                                                                        String directorName) throws DocumentException, IOException {

        Document document = new Document(PageSize.A4, 20, 20, 40, 20); // Margin lebih kecil untuk muat lebih banyak kolom
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();

            // Add header with logo and company info
            addHeader(document, logoPath, companyName, companyAddress);

            // Add separator line
            addSeparatorLine(document);

            // Add title
            addTitle(document, "LAPORAN DATA PENILAIAN KARYAWAN");

            // Add some space
            document.add(new Paragraph(" "));

            // Add table with penilaian karyawan data
            addPenilaianKaryawanTable(document, penilaianList);

            // Add signature section with less spacing
            addSignatureCompact(document, printDate, directorName);

            document.close();
        } catch (Exception e) {
            throw e;
        }

        return outputStream;
    }


    private static void addTableHeaderSmall(PdfPTable table, String headerText) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(3);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph para = new Paragraph(headerText, FONT_SMALL);
        para.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(para);

        table.addCell(cell);
    }

    private static void addTableCellSmall(PdfPTable table, String text, int alignment) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(3);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph para = new Paragraph(text != null ? text : "", FONT_SMALL);
        para.setAlignment(alignment);
        cell.addElement(para);

        table.addCell(cell);
    }

    private static void addSignatureCompact(Document document, String printDate, String directorName) throws DocumentException {
        // Add less space before signature for compact layout
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
        Paragraph datePara = new Paragraph("Jakarta, " + printDate, FONT_SMALL);
        datePara.setAlignment(Element.ALIGN_CENTER);
        rightCell.addElement(datePara);

        // Add space
        rightCell.addElement(new Paragraph(" ", FONT_SMALL));

        // Position title
        Paragraph positionPara = new Paragraph("Direktur", FONT_SMALL);
        positionPara.setAlignment(Element.ALIGN_CENTER);
        rightCell.addElement(positionPara);

        // Add space for signature
        rightCell.addElement(new Paragraph(" ", FONT_SMALL));
        rightCell.addElement(new Paragraph(" ", FONT_SMALL));

        // Signature line
        Paragraph signatureLine = new Paragraph("_______________________", FONT_SMALL);
        signatureLine.setAlignment(Element.ALIGN_CENTER);
        rightCell.addElement(signatureLine);

        // Director name
        Paragraph namePara = new Paragraph(directorName, FONT_SMALL);
        namePara.setAlignment(Element.ALIGN_CENTER);
        rightCell.addElement(namePara);

        signatureTable.addCell(rightCell);
        document.add(signatureTable);
    }

    public static ByteArrayOutputStream generatePengumumanPeringkatReport(List<id.co.lua.pbj.penilaian_karyawan.model.apps.PenilaianKaryawan> peringkatList,
                                                                          String logoPath,
                                                                          String companyName,
                                                                          String companyAddress,
                                                                          String printDate,
                                                                          String directorName,
                                                                          Integer bulan,
                                                                          Integer tahun) throws DocumentException, IOException {

        Document document = new Document(PageSize.A4, 20, 20, 40, 20);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();

            // Add header with logo and company info
            addHeader(document, logoPath, companyName, companyAddress);

            // Add separator line
            addSeparatorLine(document);

            // Add title with periode
            String[] bulanNames = {"", "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
            String titleText = "PENGUMUMAN PERINGKAT KARYAWAN";
            if (bulan != null && tahun != null && bulan > 0 && bulan <= 12) {
                titleText += "\nPeriode: " + bulanNames[bulan] + " " + tahun;
            }
            addTitle(document, titleText);

            // Add some space
            document.add(new Paragraph(" "));

            // Add table with peringkat data
            addPengumumanPeringkatTable(document, peringkatList);

            // Add signature section with less spacing
            addSignatureCompact(document, printDate, directorName);

            document.close();
        } catch (Exception e) {
            throw e;
        }

        return outputStream;
    }

    private static void addPengumumanPeringkatTable(Document document, List<id.co.lua.pbj.penilaian_karyawan.model.apps.PenilaianKaryawan> peringkatList) throws DocumentException {
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        try {
            table.setWidths(new float[]{0.7f, 1.1f, 2.5f, 1.8f, 1.8f, 1.3f, 1.3f});
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        // Add table headers with smaller font
        addTableHeaderSmall(table, "Peringkat");
        addTableHeaderSmall(table, "NIK");
        addTableHeaderSmall(table, "Nama Karyawan");
        addTableHeaderSmall(table, "Divisi");
        addTableHeaderSmall(table, "Jabatan");
        addTableHeaderSmall(table, "Bulan");
        addTableHeaderSmall(table, "Nilai");

        // Add table data
        int peringkat = 1;
        String[] bulanNames = {"", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des"};

        for (id.co.lua.pbj.penilaian_karyawan.model.apps.PenilaianKaryawan penilaian : peringkatList) {
            addTableCellSmall(table, String.valueOf(peringkat++), Element.ALIGN_CENTER);
            addTableCellSmall(table, penilaian.getKaryawan() != null ? penilaian.getKaryawan().getNik() : "-", Element.ALIGN_CENTER);
            addTableCellSmall(table, penilaian.getKaryawan() != null ? penilaian.getKaryawan().getNamaKaryawan() : "-", Element.ALIGN_LEFT);
            addTableCellSmall(table, penilaian.getDivisi() != null ? penilaian.getDivisi().getNamaDivisi() : "-", Element.ALIGN_LEFT);
            addTableCellSmall(table, penilaian.getJabatan() != null ? penilaian.getJabatan().getNamaJabatan() : "-", Element.ALIGN_LEFT);

            // Bulan
            String bulan = "-";
            if (penilaian.getBulan() != null && penilaian.getBulan() > 0 && penilaian.getBulan() <= 12) {
                bulan = bulanNames[penilaian.getBulan()];
            }
            addTableCellSmall(table, bulan, Element.ALIGN_CENTER);

            addTableCellSmall(table, penilaian.getNilaiRataRata() != null ? String.format("%.2f", penilaian.getNilaiRataRata()) : "-", Element.ALIGN_CENTER);
        }

        document.add(table);
    }

    public static ByteArrayOutputStream generateRekapTahunanReport(List<id.co.lua.pbj.penilaian_karyawan.model.dto.RekapTahunanDTO> rekapList,
                                                                   String logoPath,
                                                                   String companyName,
                                                                   String companyAddress,
                                                                   String printDate,
                                                                   String directorName,
                                                                   Integer tahun) throws DocumentException, IOException {

        Document document = new Document(PageSize.A4, 20, 20, 40, 20);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();

            // Add header with logo and company info
            addHeader(document, logoPath, companyName, companyAddress);

            // Add separator line
            addSeparatorLine(document);

            // Add title with tahun
            String titleText = "LAPORAN REKAP PENILAIAN TAHUNAN";
            if (tahun != null) {
                titleText += "\nTahun: " + tahun;
            }
            addTitle(document, titleText);

            // Add some space
            document.add(new Paragraph(" "));

            // Add table with rekap data
            addRekapTahunanTable(document, rekapList);

            // Add signature section with less spacing
            addSignatureCompact(document, printDate, directorName);

            document.close();
        } catch (Exception e) {
            throw e;
        }

        return outputStream;
    }

    private static void addRekapTahunanTable(Document document, List<id.co.lua.pbj.penilaian_karyawan.model.dto.RekapTahunanDTO> rekapList) throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        try {
            table.setWidths(new float[]{0.7f, 3.2f, 2f, 2f, 2.5f});
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        // Add table headers with smaller font
        addTableHeaderSmall(table, "No");
        addTableHeaderSmall(table, "Nama Karyawan");
        addTableHeaderSmall(table, "Divisi");
        addTableHeaderSmall(table, "Jabatan");
        addTableHeaderSmall(table, "Total Bobot (12 Bulan)");

        // Add table data
        int no = 1;
        for (id.co.lua.pbj.penilaian_karyawan.model.dto.RekapTahunanDTO rekap : rekapList) {
            addTableCellSmall(table, String.valueOf(no++), Element.ALIGN_CENTER);
            addTableCellSmall(table, rekap.getNamaKaryawan() != null ? rekap.getNamaKaryawan() : "-", Element.ALIGN_LEFT);
            addTableCellSmall(table, rekap.getNamaDivisi() != null ? rekap.getNamaDivisi() : "-", Element.ALIGN_LEFT);
            addTableCellSmall(table, rekap.getNamaJabatan() != null ? rekap.getNamaJabatan() : "-", Element.ALIGN_LEFT);
            addTableCellSmall(table, rekap.getTotalBobotTahunan() != null ? String.format("%.1f", rekap.getTotalBobotTahunan()) : "-", Element.ALIGN_CENTER);
        }

        document.add(table);
    }

    public static ByteArrayOutputStream generateNormalisasiReport(List<id.co.lua.pbj.penilaian_karyawan.model.apps.Normalisasi> normalisasiList,
                                                                   String logoPath,
                                                                   String companyName,
                                                                   String companyAddress,
                                                                   String printDate,
                                                                   String directorName,
                                                                   Integer tahun,
                                                                   Integer bulan) throws DocumentException, IOException {

        Document document = new Document(PageSize.A4.rotate(), 20, 20, 40, 20);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();

            // Add header with logo and company info
            addHeader(document, logoPath, companyName, companyAddress);

            // Add separator line
            addSeparatorLine(document);

            // Add title with tahun/bulan
            String titleText = "LAPORAN DATA NORMALISASI PENILAIAN KARYAWAN";
            if (tahun != null && bulan != null) {
                String[] namaBulan = {"", "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                                     "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
                titleText += "\nPeriode: " + namaBulan[bulan] + " " + tahun;
            } else if (tahun != null) {
                titleText += "\nTahun: " + tahun;
            }
            addTitle(document, titleText);

            // Add some space
            document.add(new Paragraph(" "));

            // Add table with normalisasi data
            addNormalisasiTable(document, normalisasiList);

            // Add signature section with less spacing
            addSignatureCompact(document, printDate, directorName);

            document.close();
        } catch (Exception e) {
            throw e;
        }

        return outputStream;
    }

    private static void addNormalisasiTable(Document document, List<id.co.lua.pbj.penilaian_karyawan.model.apps.Normalisasi> normalisasiList) throws DocumentException {
        PdfPTable table = new PdfPTable(11);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        try {
            table.setWidths(new float[]{0.7f, 2.5f, 1.8f, 1.8f, 1.5f, 1f, 1f, 1f, 1f, 1f, 1.2f});
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        // Add table headers with smaller font
        addTableHeaderSmall(table, "No");
        addTableHeaderSmall(table, "Nama Karyawan");
        addTableHeaderSmall(table, "Divisi");
        addTableHeaderSmall(table, "Jabatan");
        addTableHeaderSmall(table, "Periode");
        addTableHeaderSmall(table, "K1");
        addTableHeaderSmall(table, "K2");
        addTableHeaderSmall(table, "K3");
        addTableHeaderSmall(table, "K4");
        addTableHeaderSmall(table, "K5");
        addTableHeaderSmall(table, "Total");

        // Add table data
        int no = 1;
        for (id.co.lua.pbj.penilaian_karyawan.model.apps.Normalisasi normalisasi : normalisasiList) {
            addTableCellSmall(table, String.valueOf(no++), Element.ALIGN_CENTER);
            addTableCellSmall(table, normalisasi.getKaryawan() != null ? normalisasi.getKaryawan().getNamaKaryawan() : "-", Element.ALIGN_LEFT);
            addTableCellSmall(table, normalisasi.getDivisi() != null ? normalisasi.getDivisi().getNamaDivisi() : "-", Element.ALIGN_LEFT);
            addTableCellSmall(table, normalisasi.getJabatan() != null ? normalisasi.getJabatan().getNamaJabatan() : "-", Element.ALIGN_LEFT);

            String periode = "-";
            if (normalisasi.getBulan() != null && normalisasi.getTahun() != null) {
                periode = String.format("%02d/%d", normalisasi.getBulan(), normalisasi.getTahun());
            }
            addTableCellSmall(table, periode, Element.ALIGN_CENTER);

            addTableCellSmall(table, normalisasi.getK1Normalisasi() != null ? String.format("%.2f", normalisasi.getK1Normalisasi()) : "-", Element.ALIGN_CENTER);
            addTableCellSmall(table, normalisasi.getK2Normalisasi() != null ? String.format("%.2f", normalisasi.getK2Normalisasi()) : "-", Element.ALIGN_CENTER);
            addTableCellSmall(table, normalisasi.getK3Normalisasi() != null ? String.format("%.2f", normalisasi.getK3Normalisasi()) : "-", Element.ALIGN_CENTER);
            addTableCellSmall(table, normalisasi.getK4Normalisasi() != null ? String.format("%.2f", normalisasi.getK4Normalisasi()) : "-", Element.ALIGN_CENTER);
            addTableCellSmall(table, normalisasi.getK5Normalisasi() != null ? String.format("%.2f", normalisasi.getK5Normalisasi()) : "-", Element.ALIGN_CENTER);
            addTableCellSmall(table, normalisasi.getTotalNormalisasi() != null ? String.format("%.2f", normalisasi.getTotalNormalisasi()) : "-", Element.ALIGN_CENTER);
        }

        document.add(table);
    }

    public static ByteArrayOutputStream generatePerhitunganSkwReport(List<PenilaianKaryawan> penilaianList,
                                                                      List<Normalisasi> normalisasiList,
                                                                      List<NilaiReferensiDTO> nilaiReferensiList,
                                                                      String logoPath,
                                                                      String companyName,
                                                                      String companyAddress,
                                                                      String printDate,
                                                                      String directorName,
                                                                      Integer tahun) throws DocumentException, IOException {

        Document document = new Document(PageSize.A4.rotate(), 36, 36, 54, 36);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();

            // Add header with logo and company info
            addHeader(document, logoPath, companyName, companyAddress);

            // Add separator line
            addSeparatorLine(document);

            // Add title
            String title = "LAPORAN PERHITUNGAN SKW (SIMPLE ADDITIVE WEIGHTING)";
            if (tahun != null) {
                title += " - TAHUN " + tahun;
            }
            addTitle(document, title);

            // Add some space
            document.add(new Paragraph(" "));

            // Section 1: Penilaian Karyawan
            Paragraph section1Title = new Paragraph("1. DATA PENILAIAN KARYAWAN", FONT_HEADER);
            section1Title.setSpacingBefore(5f);
            section1Title.setSpacingAfter(5f);
            document.add(section1Title);
            addPenilaianKaryawanTable(document, penilaianList);

            // Add page break
            document.newPage();

            // Re-add header on new page
            addHeader(document, logoPath, companyName, companyAddress);
            addSeparatorLine(document);

            // Section 2: Normalisasi
            Paragraph section2Title = new Paragraph("2. DATA NORMALISASI", FONT_HEADER);
            section2Title.setSpacingBefore(5f);
            section2Title.setSpacingAfter(5f);
            document.add(section2Title);
            addNormalisasiTableForSkw(document, normalisasiList);

            // Add page break
            document.newPage();

            // Re-add header on new page
            addHeader(document, logoPath, companyName, companyAddress);
            addSeparatorLine(document);

            // Section 3: Nilai Akhir (Nilai Referensi)
            Paragraph section3Title = new Paragraph("3. NILAI AKHIR (NILAI REFERENSI)", FONT_HEADER);
            section3Title.setSpacingBefore(5f);
            section3Title.setSpacingAfter(5f);
            document.add(section3Title);

            // Add info about formula
            Paragraph formulaPara = new Paragraph(
                "Formula: Nilai Referensi = (K1 × Bobot K1) + (K2 × Bobot K2) + (K3 × Bobot K3) + (K4 × Bobot K4) + (K5 × Bobot K5)",
                FONT_SMALL
            );
            formulaPara.setAlignment(Element.ALIGN_CENTER);
            formulaPara.setSpacingAfter(5f);
            document.add(formulaPara);

            addPerhitunganSkwTable(document, nilaiReferensiList);

            // Add signature section
            addSignature(document, printDate, directorName);

            document.close();
        } catch (Exception e) {
            throw e;
        }

        return outputStream;
    }

    private static void addPenilaianKaryawanTable(Document document, List<PenilaianKaryawan> penilaianList) throws DocumentException {
        PdfPTable table = new PdfPTable(10);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        try {
            table.setWidths(new float[]{0.5f, 1.2f, 1.5f, 2f, 1.5f, 0.8f, 0.8f, 0.8f, 0.8f, 0.8f});
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        // Header row
        addTableHeaderSmall(table, "No");
        addTableHeaderSmall(table, "Periode");
        addTableHeaderSmall(table, "Divisi");
        addTableHeaderSmall(table, "Nama Karyawan");
        addTableHeaderSmall(table, "Jabatan");
        addTableHeaderSmall(table, "K1");
        addTableHeaderSmall(table, "K2");
        addTableHeaderSmall(table, "K3");
        addTableHeaderSmall(table, "K4");
        addTableHeaderSmall(table, "K5");

        // Data rows
        int no = 1;
        for (PenilaianKaryawan penilaian : penilaianList) {
            addTableCellSmall(table, String.valueOf(no++), Element.ALIGN_CENTER);

            // Periode
            String periode = "-";
            if (penilaian.getBulan() != null && penilaian.getTahun() != null) {
                String[] bulanNames = {"", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
                periode = bulanNames[penilaian.getBulan()] + " " + penilaian.getTahun();
            }
            addTableCellSmall(table, periode, Element.ALIGN_CENTER);

            // Divisi
            String divisi = "-";
            if (penilaian.getDivisi() != null) {
                divisi = penilaian.getDivisi().getNamaDivisi();
            } else if (penilaian.getKaryawan() != null && penilaian.getKaryawan().getDivisi() != null) {
                divisi = penilaian.getKaryawan().getDivisi().getNamaDivisi();
            }
            addTableCellSmall(table, divisi, Element.ALIGN_LEFT);

            // Nama Karyawan
            String namaKaryawan = penilaian.getKaryawan() != null ? penilaian.getKaryawan().getNamaKaryawan() : "-";
            addTableCellSmall(table, namaKaryawan, Element.ALIGN_LEFT);

            // Jabatan
            String jabatan = "-";
            if (penilaian.getJabatan() != null) {
                jabatan = penilaian.getJabatan().getNamaJabatan();
            } else if (penilaian.getKaryawan() != null && penilaian.getKaryawan().getJabatan() != null) {
                jabatan = penilaian.getKaryawan().getJabatan().getNamaJabatan();
            }
            addTableCellSmall(table, jabatan, Element.ALIGN_LEFT);

            // Kriteria values
            addTableCellSmall(table, penilaian.getK1() != null ? String.valueOf(penilaian.getK1()) : "-", Element.ALIGN_CENTER);
            addTableCellSmall(table, penilaian.getK2() != null ? String.valueOf(penilaian.getK2()) : "-", Element.ALIGN_CENTER);
            addTableCellSmall(table, penilaian.getK3() != null ? String.valueOf(penilaian.getK3()) : "-", Element.ALIGN_CENTER);
            addTableCellSmall(table, penilaian.getK4() != null ? String.valueOf(penilaian.getK4()) : "-", Element.ALIGN_CENTER);
            addTableCellSmall(table, penilaian.getK5() != null ? String.valueOf(penilaian.getK5()) : "-", Element.ALIGN_CENTER);
        }

        document.add(table);
    }

    private static void addNormalisasiTableForSkw(Document document, List<Normalisasi> normalisasiList) throws DocumentException {
        PdfPTable table = new PdfPTable(10);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        try {
            table.setWidths(new float[]{0.5f, 1.2f, 1.5f, 2f, 1.5f, 0.8f, 0.8f, 0.8f, 0.8f, 0.8f});
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        // Header row
        addTableHeaderSmall(table, "No");
        addTableHeaderSmall(table, "Periode");
        addTableHeaderSmall(table, "Divisi");
        addTableHeaderSmall(table, "Nama Karyawan");
        addTableHeaderSmall(table, "Jabatan");
        addTableHeaderSmall(table, "K1 (N)");
        addTableHeaderSmall(table, "K2 (N)");
        addTableHeaderSmall(table, "K3 (N)");
        addTableHeaderSmall(table, "K4 (N)");
        addTableHeaderSmall(table, "K5 (N)");

        // Data rows
        int no = 1;
        for (Normalisasi normalisasi : normalisasiList) {
            addTableCellSmall(table, String.valueOf(no++), Element.ALIGN_CENTER);

            // Periode
            String periode = "-";
            if (normalisasi.getBulan() != null && normalisasi.getTahun() != null) {
                String[] bulanNames = {"", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
                periode = bulanNames[normalisasi.getBulan()] + " " + normalisasi.getTahun();
            }
            addTableCellSmall(table, periode, Element.ALIGN_CENTER);

            // Divisi
            String divisi = "-";
            if (normalisasi.getDivisi() != null) {
                divisi = normalisasi.getDivisi().getNamaDivisi();
            } else if (normalisasi.getKaryawan() != null && normalisasi.getKaryawan().getDivisi() != null) {
                divisi = normalisasi.getKaryawan().getDivisi().getNamaDivisi();
            }
            addTableCellSmall(table, divisi, Element.ALIGN_LEFT);

            // Nama Karyawan
            String namaKaryawan = normalisasi.getKaryawan() != null ? normalisasi.getKaryawan().getNamaKaryawan() : "-";
            addTableCellSmall(table, namaKaryawan, Element.ALIGN_LEFT);

            // Jabatan
            String jabatan = "-";
            if (normalisasi.getJabatan() != null) {
                jabatan = normalisasi.getJabatan().getNamaJabatan();
            } else if (normalisasi.getKaryawan() != null && normalisasi.getKaryawan().getJabatan() != null) {
                jabatan = normalisasi.getKaryawan().getJabatan().getNamaJabatan();
            }
            addTableCellSmall(table, jabatan, Element.ALIGN_LEFT);

            // Normalisasi values
            addTableCellSmall(table, normalisasi.getK1Normalisasi() != null ? String.format("%.2f", normalisasi.getK1Normalisasi()) : "-", Element.ALIGN_CENTER);
            addTableCellSmall(table, normalisasi.getK2Normalisasi() != null ? String.format("%.2f", normalisasi.getK2Normalisasi()) : "-", Element.ALIGN_CENTER);
            addTableCellSmall(table, normalisasi.getK3Normalisasi() != null ? String.format("%.2f", normalisasi.getK3Normalisasi()) : "-", Element.ALIGN_CENTER);
            addTableCellSmall(table, normalisasi.getK4Normalisasi() != null ? String.format("%.2f", normalisasi.getK4Normalisasi()) : "-", Element.ALIGN_CENTER);
            addTableCellSmall(table, normalisasi.getK5Normalisasi() != null ? String.format("%.2f", normalisasi.getK5Normalisasi()) : "-", Element.ALIGN_CENTER);
        }

        document.add(table);
    }

    private static void addPerhitunganSkwTable(Document document, List<NilaiReferensiDTO> nilaiReferensiList) throws DocumentException {
        PdfPTable table = new PdfPTable(11);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        try {
            table.setWidths(new float[]{0.5f, 1.2f, 1.5f, 2f, 1.5f, 1f, 1f, 1f, 1f, 1f, 1.2f});
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        // Header row
        addTableHeaderSmall(table, "No");
        addTableHeaderSmall(table, "Periode");
        addTableHeaderSmall(table, "Divisi");
        addTableHeaderSmall(table, "Nama Karyawan");
        addTableHeaderSmall(table, "Jabatan");
        addTableHeaderSmall(table, "K1 × B1");
        addTableHeaderSmall(table, "K2 × B2");
        addTableHeaderSmall(table, "K3 × B3");
        addTableHeaderSmall(table, "K4 × B4");
        addTableHeaderSmall(table, "K5 × B5");
        addTableHeaderSmall(table, "Nilai Referensi");

        // Data rows
        int no = 1;
        for (NilaiReferensiDTO nilai : nilaiReferensiList) {
            addTableCellSmall(table, String.valueOf(no++), Element.ALIGN_CENTER);

            // Periode
            String periode = "-";
            if (nilai.getBulan() != null && nilai.getTahun() != null) {
                String[] bulanNames = {"", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
                periode = bulanNames[nilai.getBulan()] + " " + nilai.getTahun();
            }
            addTableCellSmall(table, periode, Element.ALIGN_CENTER);

            // Divisi
            addTableCellSmall(table, nilai.getNamaDivisi() != null ? nilai.getNamaDivisi() : "-", Element.ALIGN_LEFT);

            // Nama Karyawan
            addTableCellSmall(table, nilai.getNamaKaryawan() != null ? nilai.getNamaKaryawan() : "-", Element.ALIGN_LEFT);

            // Jabatan
            addTableCellSmall(table, nilai.getNamaJabatan() != null ? nilai.getNamaJabatan() : "-", Element.ALIGN_LEFT);

            // K1 × B1
            String k1Hasil = nilai.getHasilK1() != null ? String.format("%.2f", nilai.getHasilK1()) : "-";
            addTableCellSmall(table, k1Hasil, Element.ALIGN_CENTER);

            // K2 × B2
            String k2Hasil = nilai.getHasilK2() != null ? String.format("%.2f", nilai.getHasilK2()) : "-";
            addTableCellSmall(table, k2Hasil, Element.ALIGN_CENTER);

            // K3 × B3
            String k3Hasil = nilai.getHasilK3() != null ? String.format("%.2f", nilai.getHasilK3()) : "-";
            addTableCellSmall(table, k3Hasil, Element.ALIGN_CENTER);

            // K4 × B4
            String k4Hasil = nilai.getHasilK4() != null ? String.format("%.2f", nilai.getHasilK4()) : "-";
            addTableCellSmall(table, k4Hasil, Element.ALIGN_CENTER);

            // K5 × B5
            String k5Hasil = nilai.getHasilK5() != null ? String.format("%.2f", nilai.getHasilK5()) : "-";
            addTableCellSmall(table, k5Hasil, Element.ALIGN_CENTER);

            // Nilai Referensi (highlighted)
            String nilaiReferensi = nilai.getNilaiReferensi() != null ? String.format("%.2f", nilai.getNilaiReferensi()) : "-";
            PdfPCell cell = new PdfPCell(new Phrase(nilaiReferensi, FONT_HEADER));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(5);
            cell.setBackgroundColor(new BaseColor(220, 255, 220)); // Light green background
            table.addCell(cell);
        }

        document.add(table);
    }
}



