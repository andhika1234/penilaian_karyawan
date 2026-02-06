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
        addTableHeader(table, "Kode Kriteria");
        addTableHeader(table, "Nama Kriteria");
        addTableHeader(table, "Status");

        // Add table data
        int no = 1;
        for (id.co.lua.pbj.penilaian_karyawan.model.apps.KriteriaPenilaian kriteria : kriteriaList) {
            addTableCell(table, String.valueOf(no++), Element.ALIGN_CENTER);
            addTableCell(table, kriteria.getKodeKriteria(), Element.ALIGN_CENTER);
            addTableCell(table, kriteria.getNamaKriteria(), Element.ALIGN_LEFT);
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

    private static void addPenilaianKaryawanTable(Document document, List<id.co.lua.pbj.penilaian_karyawan.model.apps.PenilaianKaryawan> penilaianList) throws DocumentException {
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        try {
            table.setWidths(new float[]{0.6f, 2.5f, 1.8f, 1.5f, 1.5f, 1.3f, 1.5f});
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        // Add table headers with smaller font
        addTableHeaderSmall(table, "No");
        addTableHeaderSmall(table, "Nama Karyawan");
        addTableHeaderSmall(table, "Divisi");
        addTableHeaderSmall(table, "Jabatan");
        addTableHeaderSmall(table, "Periode");
        addTableHeaderSmall(table, "Nilai");
        addTableHeaderSmall(table, "Kategori");

        // Add table data
        int no = 1;
        String[] bulanNames = {"", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des"};

        for (id.co.lua.pbj.penilaian_karyawan.model.apps.PenilaianKaryawan penilaian : penilaianList) {
            addTableCellSmall(table, String.valueOf(no++), Element.ALIGN_CENTER);
            addTableCellSmall(table, penilaian.getKaryawan() != null ? penilaian.getKaryawan().getNamaKaryawan() : "-", Element.ALIGN_LEFT);
            addTableCellSmall(table, penilaian.getDivisi() != null ? penilaian.getDivisi().getNamaDivisi() : "-", Element.ALIGN_LEFT);
            addTableCellSmall(table, penilaian.getJabatan() != null ? penilaian.getJabatan().getNamaJabatan() : "-", Element.ALIGN_LEFT);

            // Periode (Bulan-Tahun)
            String periode = "-";
            if (penilaian.getBulan() != null && penilaian.getTahun() != null &&
                penilaian.getBulan() > 0 && penilaian.getBulan() <= 12) {
                periode = bulanNames[penilaian.getBulan()] + " " + penilaian.getTahun();
            }
            addTableCellSmall(table, periode, Element.ALIGN_CENTER);

            addTableCellSmall(table, penilaian.getNilaiRataRata() != null ? String.format("%.2f", penilaian.getNilaiRataRata()) : "-", Element.ALIGN_CENTER);
            addTableCellSmall(table, penilaian.getKategoriPenilaian() != null ? penilaian.getKategoriPenilaian() : "-", Element.ALIGN_CENTER);
        }

        document.add(table);
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
}

