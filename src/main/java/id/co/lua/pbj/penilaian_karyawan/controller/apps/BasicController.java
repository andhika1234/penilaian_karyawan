package id.co.lua.pbj.penilaian_karyawan.controller.apps;

import id.co.lua.pbj.penilaian_karyawan.model.accounts.User;
import id.co.lua.pbj.penilaian_karyawan.services.models.UserServices;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Calendar;
import java.util.Date;

@ControllerAdvice
public class BasicController {
    @Autowired
    UserServices userService;

    public User userLogin;


    protected Date serverTime;


    public final static Logger logger = LoggerFactory.getLogger(BasicController.class);

    @ModelAttribute
    public void userLogin(Model model, Principal principal) {

        User user = null;

        if(principal != null) {
            user = userService.getUserByUsername(principal.getName());
            userLogin = user;
        }
        int jmlhNotif = 0;
        int tahun = Calendar.getInstance().get(Calendar.YEAR);

        model.addAttribute("tahun", tahun);
        model.addAttribute("userLogin", user);
        model.addAttribute("serverTime", serverTime);
        model.addAttribute("jmlhNotif", jmlhNotif);

    }

    @ModelAttribute
    public void tahunAnggaranSession(Model model, HttpSession session) {
        Integer tahun = (Integer) session.getAttribute("tahunAnggaran");
        if(tahun!=null){
            model.addAttribute("TAHUN_ANGGARAN_SESSION",tahun);
            model.addAttribute("serverTime", serverTime);
            model.addAttribute("TAHUN_ANGGARAN", Calendar.getInstance().get(Calendar.YEAR));
        }else{
            tahun = Calendar.getInstance().get(Calendar.YEAR);
            model.addAttribute("serverTime", serverTime);
            model.addAttribute("TAHUN_ANGGARAN_SESSION",tahun);
            model.addAttribute("TAHUN_ANGGARAN",tahun);
            session.setAttribute("tahunAnggaran",tahun);
        }
    }
}