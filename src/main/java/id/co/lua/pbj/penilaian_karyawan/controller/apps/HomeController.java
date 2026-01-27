package id.co.lua.pbj.penilaian_karyawan.controller.apps;

import id.co.lua.pbj.penilaian_karyawan.helpers.GlobalMethods;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController extends BasicController{

    @RequestMapping("/")
    public ModelAndView executiveDashboard(ModelAndView mView,
                             @ModelAttribute(name = "resultCode") String resultCode,
                             @ModelAttribute(name = "resultMessage") String resultMessage, RedirectAttributes redirectAttributes) {

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