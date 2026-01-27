package id.co.lua.pbj.penilaian_karyawan.controller.apps;

import id.co.lua.pbj.penilaian_karyawan.services.models.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("role")

public class RoleController extends BasicController{

    @Autowired
    RoleService roleService;

    @GetMapping("")

    public ModelAndView index(ModelAndView mView,
                              @ModelAttribute(name = "resultCode") String resultCode,
                              @ModelAttribute(name = "resultMessage") String resultMessage) {

        mView.addObject("judul","Hak Ases Fitur");
        mView.setViewName("pages/role/role-index");
        return mView;
    }


    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         @ModelAttribute(name ="resultCode") String resultCode,
                         @ModelAttribute(name="resultMessage") String resultMessage) {

        return "redirect:/role";
    }
}

