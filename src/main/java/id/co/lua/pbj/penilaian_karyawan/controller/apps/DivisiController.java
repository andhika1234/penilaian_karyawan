package id.co.lua.pbj.penilaian_karyawan.controller.apps;

import id.co.lua.pbj.penilaian_karyawan.model.apps.Divisi;
import id.co.lua.pbj.penilaian_karyawan.services.models.DivisiService;
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
@RequestMapping("divisi")
public class DivisiController {

    @Autowired
    private DivisiService divisiService;

    @GetMapping("")
    public ModelAndView index(ModelAndView mView,
                              @ModelAttribute(name = "resultCode") String resultCode,
                              @ModelAttribute(name = "resultMessage") String resultMessage) {
        List<Divisi> divisiList = divisiService.getAllActiveDivisi();
        mView.addObject("divisiList", divisiList);
        mView.setViewName("pages/divisi/divisi-index");
        return mView;
    }

    @GetMapping("tambah")
    public ModelAndView tambah(ModelAndView mView,
                              @ModelAttribute(name = "resultCode") String resultCode,
                              @ModelAttribute(name = "resultMessage") String resultMessage) {
        mView.addObject("divisi", new Divisi());
        mView.setViewName("pages/divisi/divisi-tambah");
        return mView;
    }

    @GetMapping("edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id,
                               ModelAndView mView,
                               @ModelAttribute(name = "resultCode") String resultCode,
                               @ModelAttribute(name = "resultMessage") String resultMessage,
                               RedirectAttributes redirectAttributes) {
        Optional<Divisi> divisi = divisiService.getDivisiById(id);

        if (!divisi.isPresent()) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Divisi tidak ditemukan");
            mView.setViewName("redirect:/divisi");
            return mView;
        }

        mView.addObject("divisi", divisi.get());
        mView.setViewName("pages/divisi/divisi-edit");
        return mView;
    }

    @PostMapping("save")
    public String save(@Valid @ModelAttribute("divisi") Divisi divisi,
                      BindingResult bindingResult,
                      RedirectAttributes redirectAttributes,
                      Model model) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Data tidak valid. Silakan periksa kembali.");
            return "redirect:/divisi/tambah";
        }

        try {
            divisiService.saveDivisi(divisi);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Data divisi berhasil disimpan");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
            return "redirect:/divisi/tambah";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan saat menyimpan data");
            return "redirect:/divisi/tambah";
        }

        return "redirect:/divisi";
    }

    @PostMapping("update/{id}")
    public String update(@PathVariable("id") Long id,
                        @Valid @ModelAttribute("divisi") Divisi divisi,
                        BindingResult bindingResult,
                        RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Data tidak valid. Silakan periksa kembali.");
            return "redirect:/divisi/edit/" + id;
        }

        try {
            divisiService.updateDivisi(id, divisi);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Data divisi berhasil diupdate");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
            return "redirect:/divisi/edit/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan saat mengupdate data");
            return "redirect:/divisi/edit/" + id;
        }

        return "redirect:/divisi";
    }

    @GetMapping("delete/{id}")
    public String delete(@PathVariable("id") Long id,
                        RedirectAttributes redirectAttributes) {
        try {
            divisiService.deleteDivisi(id);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Data divisi berhasil dihapus");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan saat menghapus data");
        }

        return "redirect:/divisi";
    }

    @GetMapping("permanent-delete/{id}")
    public String permanentDelete(@PathVariable("id") Long id,
                                  RedirectAttributes redirectAttributes) {
        try {
            divisiService.permanentDeleteDivisi(id);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Data divisi berhasil dihapus permanen");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan saat menghapus data");
        }

        return "redirect:/divisi";
    }

    @GetMapping("activate/{id}")
    public String activate(@PathVariable("id") Long id,
                          RedirectAttributes redirectAttributes) {
        try {
            divisiService.activateDivisi(id);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Divisi berhasil diaktifkan");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan");
        }

        return "redirect:/divisi";
    }

    @GetMapping("deactivate/{id}")
    public String deactivate(@PathVariable("id") Long id,
                            RedirectAttributes redirectAttributes) {
        try {
            divisiService.deactivateDivisi(id);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Divisi berhasil dinonaktifkan");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan");
        }

        return "redirect:/divisi";
    }

    @GetMapping("search")
    public ModelAndView search(@RequestParam("keyword") String keyword,
                              ModelAndView mView) {
        List<Divisi> divisiList = divisiService.searchDivisi(keyword);
        mView.addObject("divisiList", divisiList);
        mView.addObject("keyword", keyword);
        mView.setViewName("pages/divisi/divisi-index");
        return mView;
    }
}
