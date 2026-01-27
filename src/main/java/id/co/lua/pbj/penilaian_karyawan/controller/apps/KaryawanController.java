package id.co.lua.pbj.penilaian_karyawan.controller.apps;

import id.co.lua.pbj.penilaian_karyawan.model.apps.Divisi;
import id.co.lua.pbj.penilaian_karyawan.model.apps.Jabatan;
import id.co.lua.pbj.penilaian_karyawan.model.apps.Karyawan;
import id.co.lua.pbj.penilaian_karyawan.services.models.DivisiService;
import id.co.lua.pbj.penilaian_karyawan.services.models.JabatanService;
import id.co.lua.pbj.penilaian_karyawan.services.models.KaryawanService;
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
@RequestMapping("karyawan")
public class KaryawanController {

    @Autowired
    private KaryawanService karyawanService;

    @Autowired
    private DivisiService divisiService;

    @Autowired
    private JabatanService jabatanService;

    @GetMapping("")
    public ModelAndView index(ModelAndView mView,
                              @ModelAttribute(name = "resultCode") String resultCode,
                              @ModelAttribute(name = "resultMessage") String resultMessage) {
        List<Karyawan> karyawanList = karyawanService.getAllActiveKaryawan();
        mView.addObject("karyawanList", karyawanList);
        mView.setViewName("pages/karyawan/karyawan-index");
        return mView;
    }

    @GetMapping("tambah")
    public ModelAndView tambah(ModelAndView mView,
                              @ModelAttribute(name = "resultCode") String resultCode,
                              @ModelAttribute(name = "resultMessage") String resultMessage) {
        mView.addObject("karyawan", new Karyawan());
        List<Divisi> divisiList = divisiService.getAllActiveDivisi();
        mView.addObject("divisiList", divisiList);
        List<Jabatan> jabatanList = jabatanService.getAllActiveJabatan();
        mView.addObject("jabatanList", jabatanList);
        mView.setViewName("pages/karyawan/karyawan-tambah");
        return mView;
    }

    @GetMapping("edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id,
                               ModelAndView mView,
                               @ModelAttribute(name = "resultCode") String resultCode,
                               @ModelAttribute(name = "resultMessage") String resultMessage,
                               RedirectAttributes redirectAttributes) {
        Optional<Karyawan> karyawan = karyawanService.getKaryawanById(id);

        if (!karyawan.isPresent()) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Karyawan tidak ditemukan");
            mView.setViewName("redirect:/karyawan");
            return mView;
        }

        mView.addObject("karyawan", karyawan.get());
        List<Divisi> divisiList = divisiService.getAllActiveDivisi();
        mView.addObject("divisiList", divisiList);
        List<Jabatan> jabatanList = jabatanService.getAllActiveJabatan();
        mView.addObject("jabatanList", jabatanList);
        mView.setViewName("pages/karyawan/karyawan-edit");
        return mView;
    }

    @PostMapping("save")
    public String save(@Valid @ModelAttribute("karyawan") Karyawan karyawan,
                      @RequestParam("divisiId") Long divisiId,
                      @RequestParam("jabatanId") Long jabatanId,
                      BindingResult bindingResult,
                      RedirectAttributes redirectAttributes,
                      Model model) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Data tidak valid. Silakan periksa kembali.");
            return "redirect:/karyawan/tambah";
        }

        try {
            // Set divisi relationship
            Optional<Divisi> divisi = divisiService.getDivisiById(divisiId);
            if (!divisi.isPresent()) {
                redirectAttributes.addFlashAttribute("resultCode", "error");
                redirectAttributes.addFlashAttribute("resultMessage", "Divisi tidak ditemukan");
                return "redirect:/karyawan/tambah";
            }
            karyawan.setDivisi(divisi.get());

            // Set jabatan relationship
            Optional<Jabatan> jabatan = jabatanService.getJabatanById(jabatanId);
            if (!jabatan.isPresent()) {
                redirectAttributes.addFlashAttribute("resultCode", "error");
                redirectAttributes.addFlashAttribute("resultMessage", "Jabatan tidak ditemukan");
                return "redirect:/karyawan/tambah";
            }
            karyawan.setJabatan(jabatan.get());

            karyawanService.saveKaryawan(karyawan);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Data karyawan berhasil disimpan");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
            return "redirect:/karyawan/tambah";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan saat menyimpan data");
            return "redirect:/karyawan/tambah";
        }

        return "redirect:/karyawan";
    }

    @PostMapping("update/{id}")
    public String update(@PathVariable("id") Long id,
                        @Valid @ModelAttribute("karyawan") Karyawan karyawan,
                        @RequestParam("divisiId") Long divisiId,
                        @RequestParam("jabatanId") Long jabatanId,
                        BindingResult bindingResult,
                        RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Data tidak valid. Silakan periksa kembali.");
            return "redirect:/karyawan/edit/" + id;
        }

        try {
            // Set divisi relationship
            Optional<Divisi> divisi = divisiService.getDivisiById(divisiId);
            if (!divisi.isPresent()) {
                redirectAttributes.addFlashAttribute("resultCode", "error");
                redirectAttributes.addFlashAttribute("resultMessage", "Divisi tidak ditemukan");
                return "redirect:/karyawan/edit/" + id;
            }
            karyawan.setDivisi(divisi.get());

            // Set jabatan relationship
            Optional<Jabatan> jabatan = jabatanService.getJabatanById(jabatanId);
            if (!jabatan.isPresent()) {
                redirectAttributes.addFlashAttribute("resultCode", "error");
                redirectAttributes.addFlashAttribute("resultMessage", "Jabatan tidak ditemukan");
                return "redirect:/karyawan/edit/" + id;
            }
            karyawan.setJabatan(jabatan.get());

            karyawanService.updateKaryawan(id, karyawan);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Data karyawan berhasil diupdate");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
            return "redirect:/karyawan/edit/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan saat mengupdate data");
            return "redirect:/karyawan/edit/" + id;
        }

        return "redirect:/karyawan";
    }

    @GetMapping("delete/{id}")
    public String delete(@PathVariable("id") Long id,
                        RedirectAttributes redirectAttributes) {
        try {
            karyawanService.deleteKaryawan(id);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Data karyawan berhasil dihapus");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan saat menghapus data");
        }

        return "redirect:/karyawan";
    }

    @GetMapping("permanent-delete/{id}")
    public String permanentDelete(@PathVariable("id") Long id,
                                  RedirectAttributes redirectAttributes) {
        try {
            karyawanService.permanentDeleteKaryawan(id);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Data karyawan berhasil dihapus permanen");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan saat menghapus data");
        }

        return "redirect:/karyawan";
    }

    @GetMapping("activate/{id}")
    public String activate(@PathVariable("id") Long id,
                          RedirectAttributes redirectAttributes) {
        try {
            karyawanService.activateKaryawan(id);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Karyawan berhasil diaktifkan");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan");
        }

        return "redirect:/karyawan";
    }

    @GetMapping("deactivate/{id}")
    public String deactivate(@PathVariable("id") Long id,
                            RedirectAttributes redirectAttributes) {
        try {
            karyawanService.deactivateKaryawan(id);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Karyawan berhasil dinonaktifkan");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan");
        }

        return "redirect:/karyawan";
    }

    @GetMapping("search")
    public ModelAndView search(@RequestParam("keyword") String keyword,
                              ModelAndView mView) {
        List<Karyawan> karyawanList = karyawanService.searchKaryawan(keyword);
        mView.addObject("karyawanList", karyawanList);
        mView.addObject("keyword", keyword);
        mView.setViewName("pages/karyawan/karyawan-index");
        return mView;
    }
}
