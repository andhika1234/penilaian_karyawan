package id.co.lua.pbj.penilaian_karyawan.controller.apps;

import id.co.lua.pbj.penilaian_karyawan.model.repositories.account.UserRepository;
import id.co.lua.pbj.penilaian_karyawan.services.models.UserServices;
import id.co.lua.pbj.penilaian_karyawan.services.security.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    final UserRepository userRepository;
    final UserServices userServices;
    final SecurityService securityService;
    final AuthenticationProvider authenticationProvider;



    public AuthController(UserServices userServices, SecurityService securityService,
                          AuthenticationProvider authenticationProvider, UserRepository userRepository) {
        this.userServices = userServices;
        this.securityService = securityService;
        this.authenticationProvider = authenticationProvider;
        this.userRepository = userRepository;

    }

    @GetMapping("/login")
    public String login() {
        return "redirect:/";
    }



    @RequestMapping("/login/{statusLogin}")
    public ModelAndView login(@PathVariable("statusLogin") String statusLogin, HttpServletRequest request, ModelMap model) {
        String error = (String) request.getSession().getAttribute("error");
        if (error != null) {
            model.addAttribute("error", error);
            request.getSession().removeAttribute("error");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return new ModelAndView("redirect:/", model);
        }
        String viewName = "";
        switch (statusLogin) {
            case "admin":
                viewName = "auth/loginadmin";
                break;
            default:
                //kesalahan login
                break;
        }

        return new ModelAndView(viewName, model);
    }


    @GetMapping("lupapassword")
    public ModelAndView lupaPassword(ModelAndView modelAndView, @RequestParam(name = "message", required = false) String pesan,
                                     @ModelAttribute(name = "resultCode") String resultCode,
                                     @ModelAttribute(name = "resultMessage") String resultMessage) {
        String message = "";
        if (pesan != null) {
            message = pesan;
        }
        modelAndView.addObject("message", message);
        modelAndView.setViewName("auth/forgot-pwd");
        return modelAndView;
    }
}
