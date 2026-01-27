package id.co.lua.pbj.penilaian_karyawan.controller.apps;

import id.co.lua.pbj.penilaian_karyawan.model.apps.Jabatan;
import id.co.lua.pbj.penilaian_karyawan.services.models.JabatanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/jabatan")
public class JabatanController {

    @Autowired
    private JabatanService jabatanService;

    @GetMapping("")
    public ModelAndView index(ModelAndView mView,
                              @ModelAttribute(name = "resultCode") String resultCode,
                              @ModelAttribute(name = "resultMessage") String resultMessage) {
        List<Jabatan> jabatanList = jabatanService.getAllActiveJabatan();
        mView.addObject("jabatanList", jabatanList);
        mView.setViewName("pages/jabatan/jabatan-index");
        return mView;
    }

    @GetMapping("tambah")
    public ModelAndView tambah(ModelAndView mView,
                              @ModelAttribute(name = "resultCode") String resultCode,
                              @ModelAttribute(name = "resultMessage") String resultMessage) {
        mView.addObject("jabatan", new Jabatan());
        mView.setViewName("pages/jabatan/jabatan-tambah");
        return mView;
    }

    @GetMapping("edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id,
                               ModelAndView mView,
                               @ModelAttribute(name = "resultCode") String resultCode,
                               @ModelAttribute(name = "resultMessage") String resultMessage,
                               RedirectAttributes redirectAttributes) {
        Optional<Jabatan> jabatan = jabatanService.getJabatanById(id);

        if (!jabatan.isPresent()) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Jabatan tidak ditemukan");
            mView.setViewName("redirect:/jabatan");
            return mView;
        }

        mView.addObject("jabatan", jabatan.get());
        mView.setViewName("pages/jabatan/jabatan-edit");
        return mView;
    }

    @PostMapping("save")
    public String save(@Valid @ModelAttribute("jabatan") Jabatan jabatan,
                      BindingResult bindingResult,
                      RedirectAttributes redirectAttributes,
                      Model model) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Data tidak valid. Silakan periksa kembali.");
            return "redirect:/jabatan/tambah";
        }

        try {
            jabatanService.saveJabatan(jabatan);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Data jabatan berhasil disimpan");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
            return "redirect:/jabatan/tambah";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan saat menyimpan data");
            return "redirect:/jabatan/tambah";
        }

        return "redirect:/jabatan";
    }

    @PostMapping("update/{id}")
    public String update(@PathVariable("id") Long id,
                        @Valid @ModelAttribute("jabatan") Jabatan jabatan,
                        BindingResult bindingResult,
                        RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Data tidak valid. Silakan periksa kembali.");
            return "redirect:/jabatan/edit/" + id;
        }

        try {
            jabatanService.updateJabatan(id, jabatan);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Data jabatan berhasil diupdate");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
            return "redirect:/jabatan/edit/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan saat mengupdate data");
            return "redirect:/jabatan/edit/" + id;
        }

        return "redirect:/jabatan";
    }

    @GetMapping("delete/{id}")
    public String delete(@PathVariable("id") Long id,
                        RedirectAttributes redirectAttributes) {
        try {
            jabatanService.deleteJabatan(id);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Data jabatan berhasil dihapus");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan saat menghapus data");
        }

        return "redirect:/jabatan";
    }

    @GetMapping("permanent-delete/{id}")
    public String permanentDelete(@PathVariable("id") Long id,
                                  RedirectAttributes redirectAttributes) {
        try {
            jabatanService.permanentDeleteJabatan(id);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Data jabatan berhasil dihapus permanen");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan saat menghapus data");
        }

        return "redirect:/jabatan";
    }

    @GetMapping("activate/{id}")
    public String activate(@PathVariable("id") Long id,
                          RedirectAttributes redirectAttributes) {
        try {
            jabatanService.activateJabatan(id);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Jabatan berhasil diaktifkan");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan");
        }

        return "redirect:/jabatan";
    }

    @GetMapping("deactivate/{id}")
    public String deactivate(@PathVariable("id") Long id,
                            RedirectAttributes redirectAttributes) {
        try {
            jabatanService.deactivateJabatan(id);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Jabatan berhasil dinonaktifkan");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan");
        }

        return "redirect:/jabatan";
    }

    @GetMapping("search")
    public ModelAndView search(@RequestParam("keyword") String keyword,
                              ModelAndView mView) {
        List<Jabatan> jabatanList = jabatanService.searchJabatan(keyword);
        mView.addObject("jabatanList", jabatanList);
        mView.addObject("keyword", keyword);
        mView.setViewName("pages/jabatan/jabatan-index");
        return mView;
    }

}
