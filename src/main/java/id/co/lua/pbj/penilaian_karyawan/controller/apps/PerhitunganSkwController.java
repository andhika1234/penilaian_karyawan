package id.co.lua.pbj.penilaian_karyawan.controller.apps;

import id.co.lua.pbj.penilaian_karyawan.model.apps.Normalisasi;
import id.co.lua.pbj.penilaian_karyawan.model.apps.PenilaianKaryawan;
import id.co.lua.pbj.penilaian_karyawan.services.models.NormalisasiService;
import id.co.lua.pbj.penilaian_karyawan.services.models.PenilaianKaryawanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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
                              @RequestParam(value = "bulan", required = false) Integer bulan,
                              @RequestParam(value = "tahun", required = false) Integer tahun,
                              @ModelAttribute(name = "resultCode") String resultCode,
                              @ModelAttribute(name = "resultMessage") String resultMessage) {

        // Set default to current month and year if not provided
        if (bulan == null || tahun == null) {
            LocalDate now = LocalDate.now();
            bulan = (bulan == null) ? now.getMonthValue() : bulan;
            tahun = (tahun == null) ? now.getYear() : tahun;
        }

        // Get data from all three sources
        // 1. Penilaian Karyawan
        List<PenilaianKaryawan> penilaianList = penilaianKaryawanService.getAllActivePenilaian();

        // 2. Normalisasi
        List<Normalisasi> normalisasiList = normalisasiService.getAllActiveNormalisasi();

        // 3. Pengumuman Peringkat (ranking data)
        List<PenilaianKaryawan> peringkatList = penilaianKaryawanService.getPeringkatByBulanAndTahun(bulan, tahun);

        mView.addObject("penilaianList", penilaianList);
        mView.addObject("normalisasiList", normalisasiList);
        mView.addObject("peringkatList", peringkatList);
        mView.addObject("selectedBulan", bulan);
        mView.addObject("selectedTahun", tahun);
        mView.setViewName("pages/perhitunganskw/perhitunganskw-index");
        return mView;
    }
}

