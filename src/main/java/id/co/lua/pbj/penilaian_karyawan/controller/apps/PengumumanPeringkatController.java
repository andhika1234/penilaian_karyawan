package id.co.lua.pbj.penilaian_karyawan.controller.apps;

import id.co.lua.pbj.penilaian_karyawan.model.apps.PenilaianKaryawan;
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
@RequestMapping("pengumumanperingkat")
public class PengumumanPeringkatController {

    @Autowired
    private PenilaianKaryawanService penilaianKaryawanService;

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

        // Get ranking data ordered by total nilai (bobot) descending
        List<PenilaianKaryawan> peringkatList = penilaianKaryawanService.getPeringkatByBulanAndTahun(bulan, tahun);

        mView.addObject("peringkatList", peringkatList);
        mView.addObject("selectedBulan", bulan);
        mView.addObject("selectedTahun", tahun);
        mView.setViewName("pages/pengumumanperingkat/pengumumanperingkat-index");
        return mView;
    }

}
