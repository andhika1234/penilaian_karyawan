package id.co.lua.pbj.penilaian_karyawan.controller.apps;

import id.co.lua.pbj.penilaian_karyawan.model.apps.Normalisasi;
import id.co.lua.pbj.penilaian_karyawan.model.apps.PenilaianKaryawan;
import id.co.lua.pbj.penilaian_karyawan.model.dto.NilaiReferensiDTO;
import id.co.lua.pbj.penilaian_karyawan.services.models.NormalisasiService;
import id.co.lua.pbj.penilaian_karyawan.services.models.PenilaianKaryawanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("perhitunganskw")
public class PerhitunganSkwController {

    @Autowired
    private PenilaianKaryawanService penilaianKaryawanService;

    @Autowired
    private NormalisasiService normalisasiService;

    @GetMapping("")
    public ModelAndView index(ModelAndView mView,
                              @RequestParam(value = "tahun", required = false) Integer tahun,
                              @ModelAttribute(name = "resultCode") String resultCode,
                              @ModelAttribute(name = "resultMessage") String resultMessage) {

        // Set default to current year if not provided
        if (tahun == null) {
            LocalDate now = LocalDate.now();
            tahun = now.getYear();
        }

        // Get data from all three sources
        // 1. Penilaian Karyawan
        List<PenilaianKaryawan> penilaianList = penilaianKaryawanService.getAllActivePenilaian();

        // 2. Normalisasi
        List<Normalisasi> normalisasiList = normalisasiService.getAllActiveNormalisasi();

        // 3. Nilai Referensi (normalisasi * bobot) - filtered by year only
        List<NilaiReferensiDTO> nilaiReferensiList = normalisasiService.calculateNilaiReferensiByTahun(tahun);

        mView.addObject("penilaianList", penilaianList);
        mView.addObject("normalisasiList", normalisasiList);
        mView.addObject("nilaiReferensiList", nilaiReferensiList);
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
}

