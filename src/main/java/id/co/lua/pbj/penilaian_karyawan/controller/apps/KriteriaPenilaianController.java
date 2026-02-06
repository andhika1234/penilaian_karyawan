package id.co.lua.pbj.penilaian_karyawan.controller.apps;

import id.co.lua.pbj.penilaian_karyawan.model.apps.KriteriaPenilaian;
import id.co.lua.pbj.penilaian_karyawan.services.models.KriteriaPenilaianService;
import id.co.lua.pbj.penilaian_karyawan.utils.PdfGenerator;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("kriteria")
public class KriteriaPenilaianController {

    @Autowired
    private KriteriaPenilaianService kriteriaService;

    @GetMapping("")
    public ModelAndView index(ModelAndView mView,
                              @ModelAttribute(name = "resultCode") String resultCode,
                              @ModelAttribute(name = "resultMessage") String resultMessage) {
        List<KriteriaPenilaian> kriteriaList = kriteriaService.getAllActiveKriteria();
        mView.addObject("kriteriaList", kriteriaList);
        mView.setViewName("pages/kriteria/kriteria-index");
        return mView;
    }

    @GetMapping("tambah")
    public ModelAndView tambah(ModelAndView mView,
                              @ModelAttribute(name = "resultCode") String resultCode,
                              @ModelAttribute(name = "resultMessage") String resultMessage) {
        mView.addObject("kriteria", new KriteriaPenilaian());
        mView.setViewName("pages/kriteria/kriteria-tambah");
        return mView;
    }

    @GetMapping("edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id,
                               ModelAndView mView,
                               @ModelAttribute(name = "resultCode") String resultCode,
                               @ModelAttribute(name = "resultMessage") String resultMessage,
                               RedirectAttributes redirectAttributes) {
        Optional<KriteriaPenilaian> kriteria = kriteriaService.getKriteriaById(id);

        if (!kriteria.isPresent()) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Kriteria tidak ditemukan");
            mView.setViewName("redirect:/kriteria");
            return mView;
        }

        mView.addObject("kriteria", kriteria.get());
        mView.setViewName("pages/kriteria/kriteria-edit");
        return mView;
    }

    @PostMapping("save")
    public String save(@Valid @ModelAttribute("kriteria") KriteriaPenilaian kriteria,
                      BindingResult bindingResult,
                      RedirectAttributes redirectAttributes,
                      Model model) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Data tidak valid. Silakan periksa kembali.");
            return "redirect:/kriteria/tambah";
        }

        try {
            kriteriaService.saveKriteria(kriteria);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Data kriteria berhasil disimpan");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
            return "redirect:/kriteria/tambah";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan saat menyimpan data");
            return "redirect:/kriteria/tambah";
        }

        return "redirect:/kriteria";
    }

    @PostMapping("update/{id}")
    public String update(@PathVariable("id") Long id,
                        @Valid @ModelAttribute("kriteria") KriteriaPenilaian kriteria,
                        BindingResult bindingResult,
                        RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Data tidak valid. Silakan periksa kembali.");
            return "redirect:/kriteria/edit/" + id;
        }

        try {
            kriteriaService.updateKriteria(id, kriteria);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Data kriteria berhasil diupdate");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
            return "redirect:/kriteria/edit/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan saat mengupdate data");
            return "redirect:/kriteria/edit/" + id;
        }

        return "redirect:/kriteria";
    }

    @GetMapping("delete/{id}")
    public String delete(@PathVariable("id") Long id,
                        RedirectAttributes redirectAttributes) {
        try {
            kriteriaService.deleteKriteria(id);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Data kriteria berhasil dihapus");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan saat menghapus data");
        }

        return "redirect:/kriteria";
    }

    @GetMapping("permanent-delete/{id}")
    public String permanentDelete(@PathVariable("id") Long id,
                                  RedirectAttributes redirectAttributes) {
        try {
            kriteriaService.permanentDeleteKriteria(id);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Data kriteria berhasil dihapus permanen");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan saat menghapus data");
        }

        return "redirect:/kriteria";
    }

    @GetMapping("cetak-pdf")
    public void cetakPdf(HttpServletResponse response) {
        try {
            // Get all active kriteria data
            List<KriteriaPenilaian> kriteriaList = kriteriaService.getAllActiveKriteria();

            // Company information - you can customize these values
            String companyName = "PT. Lua Indonesia";
            String companyAddress = "Jln. Swadaya 1 No 52 B, RT.12/RW.10, Pejaten Timur , Pasar Minggu, Jakarta Selatan. 12510\nTelepon: 087881146327 | Email: luaindonesia@gmail.com";
            String logoPath = "/static/scholar-1.0.0/assets/images/perusahaan.png"; // Adjust path as needed

            // Get current date for signature
            SimpleDateFormat dateFormatSignature = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
            String printDate = dateFormatSignature.format(new Date());

            // Director information
            String directorName = "Ilyas. S.Kom, M.T.I";

            // Generate PDF
            ByteArrayOutputStream pdfStream = PdfGenerator.generateKriteriaReport(
                kriteriaList,
                logoPath,
                companyName,
                companyAddress,
                printDate,
                directorName
            );

            // Set response headers
            response.setContentType("application/pdf");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String fileName = "Laporan_Data_Kriteria_Penilaian_" + dateFormat.format(new Date()) + ".pdf";
            response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
            response.setContentLength(pdfStream.size());

            // Write PDF to response output stream
            OutputStream outputStream = response.getOutputStream();
            pdfStream.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
