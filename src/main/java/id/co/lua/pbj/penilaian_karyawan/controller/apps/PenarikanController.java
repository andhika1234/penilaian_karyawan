package id.co.lua.pbj.penilaian_karyawan.controller.apps;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@Controller
@RequestMapping("/penarikan")
public class PenarikanController extends BasicController{

    @GetMapping("")
    public ModelAndView penarikanindex(ModelAndView mView,
                             @ModelAttribute(name = "resultCode") String resultCode,
                             @ModelAttribute(name = "resultMessage") String resultMessage, RedirectAttributes redirectAttributes) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
        mView.setViewName("pages/penarikan/index");
        return mView;
    }
}
