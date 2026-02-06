package id.co.lua.pbj.penilaian_karyawan.controller.apps;

import id.co.lua.pbj.penilaian_karyawan.model.dto.RekapTahunanDTO;
import id.co.lua.pbj.penilaian_karyawan.services.models.PenilaianKaryawanService;
import id.co.lua.pbj.penilaian_karyawan.utils.PdfGenerator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("rekaptahunan")
public class RekapTahunanController {

    @Autowired
    private PenilaianKaryawanService penilaianKaryawanService;

    @GetMapping("")
    public ModelAndView index(ModelAndView mView,
                              @RequestParam(value = "tahun", required = false) Integer tahun,
                              @ModelAttribute(name = "resultCode") String resultCode,
                              @ModelAttribute(name = "resultMessage") String resultMessage) {

        // Set default to current year if not provided
        if (tahun == null) {
            tahun = LocalDate.now().getYear();
        }

        // Get yearly recap data ordered by total bobot descending
        List<RekapTahunanDTO> rekapList = penilaianKaryawanService.getRekapTahunanByTahun(tahun);

        mView.addObject("rekapList", rekapList);
        mView.addObject("selectedTahun", tahun);
        mView.setViewName("pages/rekaptahunan/rekaptahunan-index");
        return mView;
    }

    @GetMapping("cetak-pdf")
    public void cetakPdf(HttpServletResponse response,
                        @RequestParam(value = "tahun", required = false) Integer tahun) {
        try {
            // Set default to current year if not provided
            if (tahun == null) {
                tahun = LocalDate.now().getYear();
            }

            // Get yearly recap data ordered by rata-rata nilai descending
            List<RekapTahunanDTO> rekapList = penilaianKaryawanService.getRekapTahunanByTahun(tahun);

            // Company information - you can customize these values
            String companyName = "PT. Lua Indonesia";
            String companyAddress = "Jln. Swadaya 1 No 52 B, RT.12/RW.10, Pejaten Timur , Pasar Minggu, Jakarta Selatan. 12510\nTelepon: 087881146327 | Email: luaindonesia@gmail.com";
            String logoPath = "/static/scholar-1.0.0/assets/images/perusahaan.png";

            // Get current date for signature
            SimpleDateFormat dateFormatSignature = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
            String printDate = dateFormatSignature.format(new Date());

            // Director information
            String directorName = "Ilyas. S.Kom, M.T.I";

            // Generate PDF
            ByteArrayOutputStream pdfStream = PdfGenerator.generateRekapTahunanReport(
                rekapList,
                logoPath,
                companyName,
                companyAddress,
                printDate,
                directorName,
                tahun
            );

            // Set response headers
            response.setContentType("application/pdf");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String fileName = "Laporan_Rekap_Tahunan_" + tahun + "_" + dateFormat.format(new Date()) + ".pdf";
            response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
            response.setContentLength(pdfStream.size());

            // Write PDF to response output stream
            OutputStream outputStream = response.getOutputStream();
            pdfStream.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
