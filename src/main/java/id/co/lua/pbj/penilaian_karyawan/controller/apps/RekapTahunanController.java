package id.co.lua.pbj.penilaian_karyawan.controller.apps;

import id.co.lua.pbj.penilaian_karyawan.model.dto.RekapTahunanDTO;
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
}
