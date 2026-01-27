package id.co.lua.pbj.penilaian_karyawan.controller.apps;

import id.co.lua.pbj.penilaian_karyawan.services.PageAuthValdationService;
import id.co.lua.pbj.penilaian_karyawan.services.models.RoleService;
import id.co.lua.pbj.penilaian_karyawan.services.models.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@Controller
@RequestMapping("user")
public class UserController extends BasicController{

    @Autowired
    RoleService roleService;

    @Autowired
    UserServices userServices;

    @Autowired
    PasswordEncoder pEncoder;

    @Autowired
    PageAuthValdationService pageAuthValdationService;


    @Value("${application.domain}")
    String domain;


    @GetMapping("")
    @Secured({"ROLE_ROLES_MENU"})
    public ModelAndView user(ModelAndView mView,
                                 @ModelAttribute(name = "resultCode") String resultCode,
                                 @ModelAttribute(name = "resultMessage") String resultMessage, RedirectAttributes redirectAttributes) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
        mView.addObject("judul", "Daftar User");
        mView.setViewName("pages/user/user");
        return mView;
    }



}
