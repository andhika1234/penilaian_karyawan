package id.co.lua.pbj.penilaian_karyawan.controller.apps;

import id.co.lua.pbj.penilaian_karyawan.model.apps.Normalisasi;
import id.co.lua.pbj.penilaian_karyawan.model.apps.Karyawan;
import id.co.lua.pbj.penilaian_karyawan.services.models.NormalisasiService;
import id.co.lua.pbj.penilaian_karyawan.services.models.KaryawanService;
import id.co.lua.pbj.penilaian_karyawan.utils.PdfGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("normalisasi")
public class NormalisasiController {

    @Autowired
    private NormalisasiService normalisasiService;

    @Autowired
    private KaryawanService karyawanService;

    @GetMapping("")
    public ModelAndView index(ModelAndView mView,
                              @ModelAttribute(name = "resultCode") String resultCode,
                              @ModelAttribute(name = "resultMessage") String resultMessage) {
        List<Normalisasi> normalisasiList = normalisasiService.getAllActiveNormalisasi();
        mView.addObject("normalisasiList", normalisasiList);
        mView.setViewName("pages/normalisasi/normalisasi-index");
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

        return "redirect:/normalisasi";
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

        return "redirect:/normalisasi";
    }

    @GetMapping("delete/{id}")
    public String delete(@PathVariable("id") Long id,
                        RedirectAttributes redirectAttributes) {
        try {
            normalisasiService.deleteNormalisasi(id);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Data normalisasi berhasil dihapus");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan saat menghapus data");
        }

        return "redirect:/normalisasi";
    }

    @GetMapping("permanent-delete/{id}")
    public String permanentDelete(@PathVariable("id") Long id,
                                  RedirectAttributes redirectAttributes) {
        try {
            normalisasiService.permanentDeleteNormalisasi(id);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Data normalisasi berhasil dihapus permanen");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan saat menghapus data");
        }

        return "redirect:/normalisasi";
    }

    @GetMapping("activate/{id}")
    public String activate(@PathVariable("id") Long id,
                          RedirectAttributes redirectAttributes) {
        try {
            normalisasiService.activateNormalisasi(id);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Normalisasi berhasil diaktifkan");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan");
        }

        return "redirect:/normalisasi";
    }

    @GetMapping("deactivate/{id}")
    public String deactivate(@PathVariable("id") Long id,
                            RedirectAttributes redirectAttributes) {
        try {
            normalisasiService.deactivateNormalisasi(id);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Normalisasi berhasil dinonaktifkan");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan");
        }

        return "redirect:/normalisasi";
    }

    @GetMapping("search")
    public ModelAndView search(@RequestParam("keyword") String keyword,
                              ModelAndView mView) {
        List<Normalisasi> normalisasiList = normalisasiService.searchNormalisasi(keyword);
        mView.addObject("normalisasiList", normalisasiList);
        mView.addObject("keyword", keyword);
        mView.setViewName("pages/normalisasi/normalisasi-index");
        return mView;
    }

    @GetMapping("karyawan/{karyawanId}")
    public ModelAndView normalisasiByKaryawan(@PathVariable("karyawanId") Long karyawanId,
                                           ModelAndView mView,
                                           RedirectAttributes redirectAttributes) {
        Optional<Karyawan> karyawan = karyawanService.getKaryawanById(karyawanId);

        if (!karyawan.isPresent()) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Karyawan tidak ditemukan");
            mView.setViewName("redirect:/normalisasi");
            return mView;
        }

        List<Normalisasi> normalisasiList = normalisasiService.getNormalisasiByKaryawanId(karyawanId);
        Double averageTotal = normalisasiService.getAverageTotalNormalisasiByKaryawan(karyawanId);

        mView.addObject("karyawan", karyawan.get());
        mView.addObject("normalisasiList", normalisasiList);
        mView.addObject("averageTotal", averageTotal);
        mView.setViewName("pages/normalisasi/normalisasi-index");
        return mView;
    }

    @GetMapping("periode/{tahun}")
    public ModelAndView normalisasiByTahun(@PathVariable("tahun") Integer tahun,
                                        ModelAndView mView) {
        List<Normalisasi> normalisasiList = normalisasiService.getNormalisasiByTahun(tahun);
        mView.addObject("normalisasiList", normalisasiList);
        mView.addObject("tahun", tahun);
        mView.setViewName("pages/normalisasi/normalisasi-index");
        return mView;
    }

    @GetMapping("periode/{tahun}/{bulan}")
    public ModelAndView normalisasiByBulanTahun(@PathVariable("tahun") Integer tahun,
                                             @PathVariable("bulan") Integer bulan,
                                             ModelAndView mView) {
        List<Normalisasi> normalisasiList = normalisasiService.getNormalisasiByBulanAndTahun(bulan, tahun);
        mView.addObject("normalisasiList", normalisasiList);
        mView.addObject("tahun", tahun);
        mView.addObject("bulan", bulan);
        mView.setViewName("pages/normalisasi/normalisasi-index");
        return mView;
    }

    @GetMapping("export/pdf")
    public ResponseEntity<byte[]> exportPdf(@RequestParam(value = "tahun", required = false) Integer tahun,
                                           @RequestParam(value = "bulan", required = false) Integer bulan,
                                           @RequestParam(value = "karyawanId", required = false) Long karyawanId) {
        try {
            List<Normalisasi> normalisasiList;
            String filename = "Laporan_Normalisasi";

            // Filter data based on parameters
            if (karyawanId != null) {
                normalisasiList = normalisasiService.getNormalisasiByKaryawanId(karyawanId);
                filename += "_Karyawan_" + karyawanId;
            } else if (tahun != null && bulan != null) {
                normalisasiList = normalisasiService.getNormalisasiByBulanAndTahun(bulan, tahun);
                filename += "_" + String.format("%02d_%d", bulan, tahun);
            } else if (tahun != null) {
                normalisasiList = normalisasiService.getNormalisasiByTahun(tahun);
                filename += "_" + tahun;
            } else {
                normalisasiList = normalisasiService.getAllActiveNormalisasi();
                filename += "_Semua";
            }

            // Generate PDF
            String logoPath = "/static/scholar-1.0.0/assets/images/perusahaan.png";
            String companyName = "PT. Lua Indonesia";
            String companyAddress = "Jln. Swadaya 1 No 52 B, RT.12/RW.10, Pejaten Timur , Pasar Minggu, Jakarta Selatan. 12510\nTelepon: 087881146327 | Email: luaindonesia@gmail.com";
            String printDate = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", new Locale("id", "ID")));
            String directorName = "Ilyas. S.Kom, M.T.I";

            ByteArrayOutputStream pdfStream = PdfGenerator.generateNormalisasiReport(
                normalisasiList,
                logoPath,
                companyName,
                companyAddress,
                printDate,
                directorName,
                tahun,
                bulan
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfStream.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}

