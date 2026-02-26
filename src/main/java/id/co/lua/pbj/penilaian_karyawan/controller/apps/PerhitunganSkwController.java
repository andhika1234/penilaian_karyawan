package id.co.lua.pbj.penilaian_karyawan.controller.apps;

import id.co.lua.pbj.penilaian_karyawan.model.apps.Normalisasi;
import id.co.lua.pbj.penilaian_karyawan.model.apps.PenilaianKaryawan;
import id.co.lua.pbj.penilaian_karyawan.model.dto.NilaiReferensiDTO;
import id.co.lua.pbj.penilaian_karyawan.services.models.NormalisasiService;
import id.co.lua.pbj.penilaian_karyawan.services.models.PenilaianKaryawanService;
import id.co.lua.pbj.penilaian_karyawan.utils.PdfGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("perhitunganskw")
public class PerhitunganSkwController {

    @Autowired
    private PenilaianKaryawanService penilaianKaryawanService;

    @Autowired
    private NormalisasiService normalisasiService;

    // Using same company info as other reports
    private static final String LOGO_PATH = "/static/scholar-1.0.0/assets/images/perusahaan.png";
    private static final String COMPANY_NAME = "PT. Lua Indonesia";
    private static final String COMPANY_ADDRESS = "Jln. Swadaya 1 No 52 B, RT.12/RW.10, Pejaten Timur , Pasar Minggu, Jakarta Selatan. 12510\nTelepon: 087881146327 | Email: luaindonesia@gmail.com";
    private static final String DIRECTOR_NAME = "Ilyas. S.Kom, M.T.I";

    @GetMapping("")
    public ModelAndView index(ModelAndView mView,
                              @RequestParam(value = "bulan", required = false) Integer bulan,
                              @RequestParam(value = "tahun", required = false) Integer tahun,
                              @ModelAttribute(name = "resultCode") String resultCode,
                              @ModelAttribute(name = "resultMessage") String resultMessage) {

        // Set default to current year if not provided
        if (tahun == null) {
            LocalDate now = LocalDate.now();
            tahun = now.getYear();
        }

        // Get data filtered by bulan+tahun if bulan is selected, otherwise by tahun only
        List<PenilaianKaryawan> penilaianList;
        List<Normalisasi> normalisasiList;
        List<NilaiReferensiDTO> nilaiReferensiList;

        if (bulan != null) {
            penilaianList = penilaianKaryawanService.getPenilaianByBulanAndTahun(bulan, tahun);
            normalisasiList = normalisasiService.getNormalisasiByBulanAndTahun(bulan, tahun);
            nilaiReferensiList = normalisasiService.calculateNilaiReferensiByBulanAndTahun(bulan, tahun);
        } else {
            penilaianList = penilaianKaryawanService.getAllActivePenilaian();
            normalisasiList = normalisasiService.getAllActiveNormalisasi();
            nilaiReferensiList = normalisasiService.calculateNilaiReferensiByTahun(tahun);
        }

        mView.addObject("penilaianList", penilaianList);
        mView.addObject("normalisasiList", normalisasiList);
        mView.addObject("nilaiReferensiList", nilaiReferensiList);
        mView.addObject("selectedBulan", bulan);
        mView.addObject("selectedTahun", tahun);
        mView.setViewName("pages/perhitunganskw/perhitunganskw-index");
        return mView;
    }

    @GetMapping("generate")
    public String generateAll(RedirectAttributes redirectAttributes) {
        try {
            int count = normalisasiService.generateAllNormalisasi();
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Berhasil generate " + count + " data normalisasi");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan: " + e.getMessage());
        }

        return "redirect:/perhitunganskw";
    }

    @GetMapping("generate/{penilaianKaryawanId}")
    public String generateFromPenilaian(@PathVariable("penilaianKaryawanId") Long penilaianKaryawanId,
                                       RedirectAttributes redirectAttributes) {
        try {
            normalisasiService.saveNormalisasiFromPenilaian(penilaianKaryawanId);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Data normalisasi berhasil digenerate");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan: " + e.getMessage());
        }

        return "redirect:/perhitunganskw";
    }

    @GetMapping("export-pdf")
    public ResponseEntity<byte[]> exportPdf(@RequestParam(value = "bulan", required = false) Integer bulan,
                                            @RequestParam(value = "tahun", required = false) Integer tahun) {
        try {
            // Set default to current year if not provided
            if (tahun == null) {
                LocalDate now = LocalDate.now();
                tahun = now.getYear();
            }

            List<PenilaianKaryawan> penilaianList;
            List<Normalisasi> normalisasiList;
            List<NilaiReferensiDTO> nilaiReferensiList;

            if (bulan != null) {
                penilaianList = penilaianKaryawanService.getPenilaianByBulanAndTahun(bulan, tahun);
                normalisasiList = normalisasiService.getNormalisasiByBulanAndTahun(bulan, tahun);
                nilaiReferensiList = normalisasiService.calculateNilaiReferensiByBulanAndTahun(bulan, tahun);
            } else {
                penilaianList = penilaianKaryawanService.getAllActivePenilaian();
                normalisasiList = normalisasiService.getAllActiveNormalisasi();
                nilaiReferensiList = normalisasiService.calculateNilaiReferensiByTahun(tahun);
            }

            // Get current date for print date
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", new Locale("id", "ID"));
            String printDate = LocalDate.now().format(dateFormatter);

            // Generate PDF with all 3 tables
            ByteArrayOutputStream pdfStream = PdfGenerator.generatePerhitunganSkwReport(
                penilaianList,
                normalisasiList,
                nilaiReferensiList,
                LOGO_PATH,
                COMPANY_NAME,
                COMPANY_ADDRESS,
                printDate,
                DIRECTOR_NAME,
                tahun
            );

            // Prepare response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String fileName = bulan != null
                    ? "Laporan_Perhitungan_SKW_" + bulan + "_" + tahun + ".pdf"
                    : "Laporan_Perhitungan_SKW_" + tahun + ".pdf";
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfStream.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}

