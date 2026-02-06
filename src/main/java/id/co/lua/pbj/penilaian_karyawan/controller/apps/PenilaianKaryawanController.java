package id.co.lua.pbj.penilaian_karyawan.controller.apps;

import id.co.lua.pbj.penilaian_karyawan.model.apps.DetailPenilaian;
import id.co.lua.pbj.penilaian_karyawan.model.apps.Divisi;
import id.co.lua.pbj.penilaian_karyawan.model.apps.Jabatan;
import id.co.lua.pbj.penilaian_karyawan.model.apps.Karyawan;
import id.co.lua.pbj.penilaian_karyawan.model.apps.KriteriaPenilaian;
import id.co.lua.pbj.penilaian_karyawan.model.apps.PenilaianKaryawan;
import id.co.lua.pbj.penilaian_karyawan.services.models.DivisiService;
import id.co.lua.pbj.penilaian_karyawan.services.models.JabatanService;
import id.co.lua.pbj.penilaian_karyawan.services.models.KaryawanService;
import id.co.lua.pbj.penilaian_karyawan.services.models.KriteriaPenilaianService;
import id.co.lua.pbj.penilaian_karyawan.services.models.PenilaianKaryawanService;
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
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("penilaiankaryawan")
public class PenilaianKaryawanController {

    @Autowired
    private PenilaianKaryawanService penilaianKaryawanService;

    @Autowired
    private KaryawanService karyawanService;

    @Autowired
    private DivisiService divisiService;

    @Autowired
    private JabatanService jabatanService;

    @Autowired
    private KriteriaPenilaianService kriteriaPenilaianService;

    @GetMapping("")
    public ModelAndView index(ModelAndView mView,
                              @ModelAttribute(name = "resultCode") String resultCode,
                              @ModelAttribute(name = "resultMessage") String resultMessage) {
        List<PenilaianKaryawan> penilaianList = penilaianKaryawanService.getAllActivePenilaian();
        mView.addObject("penilaianList", penilaianList);
        mView.setViewName("pages/penilaiankaryawan/penilaiankaryawan-index");
        return mView;
    }

    @GetMapping("tambah")
    public ModelAndView tambah(ModelAndView mView,
                               @ModelAttribute(name = "resultCode") String resultCode,
                               @ModelAttribute(name = "resultMessage") String resultMessage) {
        mView.addObject("penilaian", new PenilaianKaryawan());
        List<Divisi> divisiList = divisiService.getAllActiveDivisi();
        mView.addObject("divisiList", divisiList);
        List<Karyawan> karyawanList = karyawanService.getAllActiveKaryawan();
        mView.addObject("karyawanList", karyawanList);
        List<Jabatan> jabatanList = jabatanService.getAllActiveJabatan();
        mView.addObject("jabatanList", jabatanList);
        List<KriteriaPenilaian> kriteriaList = kriteriaPenilaianService.getAllActiveKriteria();
        mView.addObject("kriteriaList", kriteriaList);
        mView.setViewName("pages/penilaiankaryawan/penilaiankaryawan-tambah");
        return mView;
    }

    @GetMapping("edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id,
                             ModelAndView mView,
                             @ModelAttribute(name = "resultCode") String resultCode,
                             @ModelAttribute(name = "resultMessage") String resultMessage,
                             RedirectAttributes redirectAttributes) {
        Optional<PenilaianKaryawan> penilaian = penilaianKaryawanService.getPenilaianById(id);

        if (!penilaian.isPresent()) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Penilaian tidak ditemukan");
            mView.setViewName("redirect:/penilaiankaryawan");
            return mView;
        }

        mView.addObject("penilaian", penilaian.get());
        List<Divisi> divisiList = divisiService.getAllActiveDivisi();
        mView.addObject("divisiList", divisiList);
        List<Karyawan> karyawanList = karyawanService.getAllActiveKaryawan();
        mView.addObject("karyawanList", karyawanList);
        List<Jabatan> jabatanList = jabatanService.getAllActiveJabatan();
        mView.addObject("jabatanList", jabatanList);
        List<KriteriaPenilaian> kriteriaList = kriteriaPenilaianService.getAllActiveKriteria();
        mView.addObject("kriteriaList", kriteriaList);
        mView.setViewName("pages/penilaiankaryawan/penilaiankaryawan-edit");
        return mView;
    }

    @PostMapping("save")
    public String save(@RequestParam("karyawanId") Long karyawanId,
                      @RequestParam("divisiId") Long divisiId,
                      @RequestParam("jabatanId") Long jabatanId,
                      @RequestParam("bulan") Integer bulan,
                      @RequestParam("tahun") Integer tahun,
                      @RequestParam Map<String, String> kriteria,
                      @RequestParam(value = "catatan", required = false) String catatan,
                      RedirectAttributes redirectAttributes) {

        try {
            // Get karyawan
            Optional<Karyawan> karyawan = karyawanService.getKaryawanById(karyawanId);
            if (!karyawan.isPresent()) {
                redirectAttributes.addFlashAttribute("resultCode", "error");
                redirectAttributes.addFlashAttribute("resultMessage", "Karyawan tidak ditemukan");
                return "redirect:/penilaiankaryawan/tambah";
            }

            // Get divisi
            Optional<Divisi> divisi = divisiService.getDivisiById(divisiId);
            if (!divisi.isPresent()) {
                redirectAttributes.addFlashAttribute("resultCode", "error");
                redirectAttributes.addFlashAttribute("resultMessage", "Divisi tidak ditemukan");
                return "redirect:/penilaiankaryawan/tambah";
            }

            // Get jabatan
            Optional<Jabatan> jabatan = jabatanService.getJabatanById(jabatanId);
            if (!jabatan.isPresent()) {
                redirectAttributes.addFlashAttribute("resultCode", "error");
                redirectAttributes.addFlashAttribute("resultMessage", "Jabatan tidak ditemukan");
                return "redirect:/penilaiankaryawan/tambah";
            }

            // Create penilaian object
            PenilaianKaryawan penilaian = new PenilaianKaryawan();
            penilaian.setKaryawan(karyawan.get());
            penilaian.setDivisi(divisi.get());
            penilaian.setJabatan(jabatan.get());
            penilaian.setBulan(bulan);
            penilaian.setTahun(tahun);
            penilaian.setCatatan(catatan);

            // Process kriteria values - create DetailPenilaian for each
            List<KriteriaPenilaian> kriteriaList = kriteriaPenilaianService.getAllActiveKriteria();
            for (KriteriaPenilaian kriteriaItem : kriteriaList) {
                String key = "kriteria[" + kriteriaItem.getNamaKriteria() + "]";
                String nilaiStr = kriteria.get(key);

                if (nilaiStr != null && !nilaiStr.isEmpty()) {
                    DetailPenilaian detail = new DetailPenilaian();
                    detail.setKriteriaPenilaian(kriteriaItem);
                    detail.setNilai(Integer.parseInt(nilaiStr));
                    penilaian.addDetailPenilaian(detail);
                }
            }

            penilaianKaryawanService.savePenilaian(penilaian);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Data penilaian berhasil disimpan");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
            return "redirect:/penilaiankaryawan/tambah";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan saat menyimpan data: " + e.getMessage());
            return "redirect:/penilaiankaryawan/tambah";
        }

        return "redirect:/penilaiankaryawan";
    }

    @PostMapping("update/{id}")
    public String update(@PathVariable("id") Long id,
                        @RequestParam("karyawanId") Long karyawanId,
                        @RequestParam("divisiId") Long divisiId,
                        @RequestParam("jabatanId") Long jabatanId,
                        @RequestParam("bulan") Integer bulan,
                        @RequestParam("tahun") Integer tahun,
                        @RequestParam Map<String, String> kriteria,
                        @RequestParam(value = "catatan", required = false) String catatan,
                        RedirectAttributes redirectAttributes) {

        try {
            // Get karyawan
            Optional<Karyawan> karyawan = karyawanService.getKaryawanById(karyawanId);
            if (!karyawan.isPresent()) {
                redirectAttributes.addFlashAttribute("resultCode", "error");
                redirectAttributes.addFlashAttribute("resultMessage", "Karyawan tidak ditemukan");
                return "redirect:/penilaiankaryawan/edit/" + id;
            }

            // Get divisi
            Optional<Divisi> divisi = divisiService.getDivisiById(divisiId);
            if (!divisi.isPresent()) {
                redirectAttributes.addFlashAttribute("resultCode", "error");
                redirectAttributes.addFlashAttribute("resultMessage", "Divisi tidak ditemukan");
                return "redirect:/penilaiankaryawan/edit/" + id;
            }

            // Get jabatan
            Optional<Jabatan> jabatan = jabatanService.getJabatanById(jabatanId);
            if (!jabatan.isPresent()) {
                redirectAttributes.addFlashAttribute("resultCode", "error");
                redirectAttributes.addFlashAttribute("resultMessage", "Jabatan tidak ditemukan");
                return "redirect:/penilaiankaryawan/edit/" + id;
            }

            // Create penilaian object
            PenilaianKaryawan penilaian = new PenilaianKaryawan();
            penilaian.setKaryawan(karyawan.get());
            penilaian.setDivisi(divisi.get());
            penilaian.setJabatan(jabatan.get());
            penilaian.setBulan(bulan);
            penilaian.setTahun(tahun);
            penilaian.setCatatan(catatan);

            // Process kriteria values - create DetailPenilaian for each
            List<KriteriaPenilaian> kriteriaList = kriteriaPenilaianService.getAllActiveKriteria();
            for (KriteriaPenilaian kriteriaItem : kriteriaList) {
                String key = "kriteria[" + kriteriaItem.getNamaKriteria() + "]";
                String nilaiStr = kriteria.get(key);

                if (nilaiStr != null && !nilaiStr.isEmpty()) {
                    DetailPenilaian detail = new DetailPenilaian();
                    detail.setKriteriaPenilaian(kriteriaItem);
                    detail.setNilai(Integer.parseInt(nilaiStr));
                    penilaian.addDetailPenilaian(detail);
                }
            }

            penilaianKaryawanService.updatePenilaian(id, penilaian);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Data penilaian berhasil diupdate");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
            return "redirect:/penilaiankaryawan/edit/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan saat mengupdate data: " + e.getMessage());
            return "redirect:/penilaiankaryawan/edit/" + id;
        }

        return "redirect:/penilaiankaryawan";
    }

    @GetMapping("delete/{id}")
    public String delete(@PathVariable("id") Long id,
                        RedirectAttributes redirectAttributes) {
        try {
            penilaianKaryawanService.deletePenilaian(id);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Data penilaian berhasil dihapus");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan saat menghapus data");
        }

        return "redirect:/penilaiankaryawan";
    }

    @GetMapping("permanent-delete/{id}")
    public String permanentDelete(@PathVariable("id") Long id,
                                  RedirectAttributes redirectAttributes) {
        try {
            penilaianKaryawanService.permanentDeletePenilaian(id);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Data penilaian berhasil dihapus permanen");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan saat menghapus data");
        }

        return "redirect:/penilaiankaryawan";
    }

    @GetMapping("activate/{id}")
    public String activate(@PathVariable("id") Long id,
                          RedirectAttributes redirectAttributes) {
        try {
            penilaianKaryawanService.activatePenilaian(id);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Penilaian berhasil diaktifkan");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan");
        }

        return "redirect:/penilaiankaryawan";
    }

    @GetMapping("deactivate/{id}")
    public String deactivate(@PathVariable("id") Long id,
                            RedirectAttributes redirectAttributes) {
        try {
            penilaianKaryawanService.deactivatePenilaian(id);
            redirectAttributes.addFlashAttribute("resultCode", "success");
            redirectAttributes.addFlashAttribute("resultMessage", "Penilaian berhasil dinonaktifkan");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Terjadi kesalahan");
        }

        return "redirect:/penilaiankaryawan";
    }

    @GetMapping("search")
    public ModelAndView search(@RequestParam("keyword") String keyword,
                              ModelAndView mView) {
        List<PenilaianKaryawan> penilaianList = penilaianKaryawanService.searchPenilaian(keyword);
        mView.addObject("penilaianList", penilaianList);
        mView.addObject("keyword", keyword);
        mView.setViewName("pages/penilaiankaryawan/penilaiankaryawan-index");
        return mView;
    }

    @GetMapping("karyawan/{karyawanId}")
    public ModelAndView penilaianByKaryawan(@PathVariable("karyawanId") Long karyawanId,
                                           ModelAndView mView,
                                           RedirectAttributes redirectAttributes) {
        Optional<Karyawan> karyawan = karyawanService.getKaryawanById(karyawanId);

        if (!karyawan.isPresent()) {
            redirectAttributes.addFlashAttribute("resultCode", "error");
            redirectAttributes.addFlashAttribute("resultMessage", "Karyawan tidak ditemukan");
            mView.setViewName("redirect:/penilaiankaryawan");
            return mView;
        }

        List<PenilaianKaryawan> penilaianList = penilaianKaryawanService.getPenilaianByKaryawanId(karyawanId);
        Double averageNilai = penilaianKaryawanService.getAverageNilaiByKaryawan(karyawanId);

        mView.addObject("karyawan", karyawan.get());
        mView.addObject("penilaianList", penilaianList);
        mView.addObject("averageNilai", averageNilai);
        mView.setViewName("pages/penilaiankaryawan/penilaiankaryawan-index");
        return mView;
    }

    @GetMapping("periode/{tahun}")
    public ModelAndView penilaianByTahun(@PathVariable("tahun") Integer tahun,
                                        ModelAndView mView) {
        List<PenilaianKaryawan> penilaianList = penilaianKaryawanService.getPenilaianByTahun(tahun);
        mView.addObject("penilaianList", penilaianList);
        mView.addObject("tahun", tahun);
        mView.setViewName("pages/penilaiankaryawan/penilaiankaryawan-index");
        return mView;
    }

    @GetMapping("periode/{tahun}/{bulan}")
    public ModelAndView penilaianByBulanTahun(@PathVariable("tahun") Integer tahun,
                                             @PathVariable("bulan") Integer bulan,
                                             ModelAndView mView) {
        List<PenilaianKaryawan> penilaianList = penilaianKaryawanService.getPenilaianByBulanAndTahun(bulan, tahun);
        mView.addObject("penilaianList", penilaianList);
        mView.addObject("tahun", tahun);
        mView.addObject("bulan", bulan);
        mView.setViewName("pages/penilaiankaryawan/penilaiankaryawan-index");
        return mView;
    }

    @GetMapping("cetak-pdf")
    public void cetakPdf(HttpServletResponse response) {
        try {
            // Get all active penilaian data
            List<PenilaianKaryawan> penilaianList = penilaianKaryawanService.getAllActivePenilaian();

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
            ByteArrayOutputStream pdfStream = PdfGenerator.generatePenilaianKaryawanReport(
                penilaianList,
                logoPath,
                companyName,
                companyAddress,
                printDate,
                directorName
            );

            // Set response headers
            response.setContentType("application/pdf");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String fileName = "Laporan_Penilaian_Karyawan_" + dateFormat.format(new Date()) + ".pdf";
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
