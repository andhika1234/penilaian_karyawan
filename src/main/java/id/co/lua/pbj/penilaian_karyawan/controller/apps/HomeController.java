package id.co.lua.pbj.penilaian_karyawan.controller.apps;

import id.co.lua.pbj.penilaian_karyawan.helpers.GlobalMethods;
import id.co.lua.pbj.penilaian_karyawan.model.apps.PenilaianKaryawan;
import id.co.lua.pbj.penilaian_karyawan.services.models.PenilaianKaryawanService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController extends BasicController{

    @Autowired
    private PenilaianKaryawanService penilaianKaryawanService;

    @RequestMapping("/")
    public ModelAndView executiveDashboard(ModelAndView mView,
                             @ModelAttribute(name = "resultCode") String resultCode,
                             @ModelAttribute(name = "resultMessage") String resultMessage, RedirectAttributes redirectAttributes) {

        // Get current month and year
        LocalDate now = LocalDate.now();
        int bulan = now.getMonthValue();
        int tahun = now.getYear();

        // Get top 3 rankings for current month
        List<PenilaianKaryawan> peringkatList = penilaianKaryawanService.getPeringkatByBulanAndTahun(bulan, tahun);

        // If no data for current month, try to find the latest available data
        if (peringkatList == null || peringkatList.isEmpty()) {
            // Try previous months in the current year first
            for (int i = bulan - 1; i >= 1; i--) {
                peringkatList = penilaianKaryawanService.getPeringkatByBulanAndTahun(i, tahun);
                if (peringkatList != null && !peringkatList.isEmpty()) {
                    bulan = i;
                    break;
                }
            }

            // If still no data, try previous year (December to January)
            if (peringkatList == null || peringkatList.isEmpty()) {
                for (int y = tahun - 1; y >= tahun - 2; y--) {
                    for (int m = 12; m >= 1; m--) {
                        peringkatList = penilaianKaryawanService.getPeringkatByBulanAndTahun(m, y);
                        if (peringkatList != null && !peringkatList.isEmpty()) {
                            bulan = m;
                            tahun = y;
                            break;
                        }
                    }
                    if (peringkatList != null && !peringkatList.isEmpty()) {
                        break;
                    }
                }
            }
        }

        // Get top 3 from the result
        List<PenilaianKaryawan> top3Peringkat;
        if (peringkatList != null && !peringkatList.isEmpty()) {
            top3Peringkat = peringkatList.stream()
                    .limit(3)
                    .collect(Collectors.toList());
        } else {
            top3Peringkat = List.of(); // Empty list if no data found
        }

        mView.addObject("top3Peringkat", top3Peringkat);
        mView.addObject("currentBulan", bulan);
        mView.addObject("currentTahun", tahun);
        mView.addObject("isCurrentMonth", bulan == now.getMonthValue() && tahun == now.getYear());
        mView.setViewName("pages/home/index");
        return mView;
    }


    @RequestMapping("/sl")
    public ModelAndView mainSuccessLogin(ModelAndView mView,
                                         @ModelAttribute(name = "resultCode") String resultCode,
                                         @ModelAttribute(name = "resultMessage") String resultMessage,
                                         RedirectAttributes redirectAttributes) {
        if (userLogin.isActive()) {
                GlobalMethods.setRedirectAttribute(redirectAttributes, "1", "Selamat Datang " + userLogin.getName() , null, null);
        } else {
            GlobalMethods.setRedirectAttribute(redirectAttributes, "0", "Akun Tidak Aktif Silahkan Buka Email Anda dan lakukan verifikasi", null, null);
        }
        mView.setViewName("redirect:/");
        return mView;
    }

    @RequestMapping("/el")
    public String loginFailed(RedirectAttributes redirectAttributes, HttpServletRequest request) {
        request.getSession().setAttribute("error", "Username atau password salah!");
        GlobalMethods.setRedirectAttribute(redirectAttributes, "-1", "Login gagal! Username atau password yang dimasukkan salah. Silahkan coba kembali", null, null);
        return "redirect:/";
    }

    @RequestMapping("/dashboardsatdik")
    public ModelAndView dashboardSatdik(ModelAndView mView,
                             @ModelAttribute(name = "resultCode") String resultCode,
                             @ModelAttribute(name = "resultMessage") String resultMessage, RedirectAttributes redirectAttributes) {

        mView.setViewName("pages/home/dashboardsatdik");
        return mView;
    }

    @RequestMapping("/dashboardpenyedia")
    public ModelAndView dashboardpenyedia(ModelAndView mView,
                                          @ModelAttribute(name = "resultCode") String resultCode,
                                          @ModelAttribute(name = "resultMessage") String resultMessage, RedirectAttributes redirectAttributes) {

        mView.setViewName("pages/home/dashboardpenyedia");
        return mView;
    }


    @RequestMapping("/pengadaanberjalan")
    public ModelAndView pengadaanberjalan(ModelAndView mView,
                                          @ModelAttribute(name = "resultCode") String resultCode,
                                          @ModelAttribute(name = "resultMessage") String resultMessage, RedirectAttributes redirectAttributes) {

        mView.setViewName("pages/home/pengadaanberjalan");
        return mView;
    }

}