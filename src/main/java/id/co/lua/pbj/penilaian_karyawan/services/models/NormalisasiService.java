package id.co.lua.pbj.penilaian_karyawan.services.models;

import id.co.lua.pbj.penilaian_karyawan.model.apps.Normalisasi;
import id.co.lua.pbj.penilaian_karyawan.model.apps.PenilaianKaryawan;
import id.co.lua.pbj.penilaian_karyawan.model.dto.NilaiReferensiDTO;
import id.co.lua.pbj.penilaian_karyawan.model.repositories.apps.NormalisasiRepository;
import id.co.lua.pbj.penilaian_karyawan.model.repositories.apps.PenilaianKaryawanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NormalisasiService {

    @Autowired
    private NormalisasiRepository normalisasiRepository;

    @Autowired
    private PenilaianKaryawanRepository penilaianKaryawanRepository;


    public List<Normalisasi> getAllActiveNormalisasi() {
        return normalisasiRepository.findByStatusAktifTrueOrderByTahunDescBulanDesc();
    }


    public Optional<Normalisasi> getNormalisasiById(Long id) {
        return normalisasiRepository.findById(id);
    }


    public Optional<Normalisasi> getNormalisasiByPenilaianKaryawanId(Long penilaianKaryawanId) {
        return normalisasiRepository.findByPenilaianKaryawanIdAndStatusAktifTrue(penilaianKaryawanId);
    }


    public List<Normalisasi> getNormalisasiByKaryawanId(Long karyawanId) {
        return normalisasiRepository.findByKaryawanIdAndStatusAktifTrueOrderByTahunDescBulanDesc(karyawanId);
    }


    public List<Normalisasi> getNormalisasiByTahun(Integer tahun) {
        return normalisasiRepository.findByTahunAndStatusAktifTrueOrderByBulanDesc(tahun);
    }


    public List<Normalisasi> getNormalisasiByBulanAndTahun(Integer bulan, Integer tahun) {
        return normalisasiRepository.findByBulanAndTahunAndStatusAktifTrue(bulan, tahun);
    }


    public List<Normalisasi> searchNormalisasi(String keyword) {
        return normalisasiRepository.searchNormalisasi(keyword);
    }

    public Normalisasi saveNormalisasiFromPenilaian(Long penilaianKaryawanId) {
        Optional<PenilaianKaryawan> penilaianOpt = penilaianKaryawanRepository.findById(penilaianKaryawanId);

        if (!penilaianOpt.isPresent()) {
            throw new IllegalArgumentException("Penilaian karyawan tidak ditemukan");
        }

        PenilaianKaryawan penilaian = penilaianOpt.get();

        // Check if normalisasi already exists for this penilaian
        Optional<Normalisasi> existingNormalisasi = normalisasiRepository.findByPenilaianKaryawanIdAndStatusAktifTrue(penilaianKaryawanId);

        Normalisasi normalisasi;
        if (existingNormalisasi.isPresent()) {
            normalisasi = existingNormalisasi.get();
        } else {
            normalisasi = new Normalisasi();
            normalisasi.setPenilaianKaryawan(penilaian);
            normalisasi.setKaryawan(penilaian.getKaryawan());
            normalisasi.setDivisi(penilaian.getDivisi());
            normalisasi.setJabatan(penilaian.getJabatan());
            normalisasi.setBulan(penilaian.getBulan());
            normalisasi.setTahun(penilaian.getTahun());
            normalisasi.setTanggalPenilaian(penilaian.getTanggalPenilaian() != null ? penilaian.getTanggalPenilaian() : LocalDate.now());
            normalisasi.setCatatan(penilaian.getCatatan());
        }

        // Calculate normalized values (nilai / 5)
        normalisasi.setK1Normalisasi(Normalisasi.normalizeValue(penilaian.getK1()));
        normalisasi.setK2Normalisasi(Normalisasi.normalizeValue(penilaian.getK2()));
        normalisasi.setK3Normalisasi(Normalisasi.normalizeValue(penilaian.getK3()));
        normalisasi.setK4Normalisasi(Normalisasi.normalizeValue(penilaian.getK4()));
        normalisasi.setK5Normalisasi(Normalisasi.normalizeValue(penilaian.getK5()));

        // Calculate total normalisasi
        normalisasi.calculateTotalNormalisasi();

        return normalisasiRepository.save(normalisasi);
    }


    public int generateAllNormalisasi() {
        List<PenilaianKaryawan> penilaianList = penilaianKaryawanRepository.findAllActivePenilaian();
        int count = 0;

        for (PenilaianKaryawan penilaian : penilaianList) {
            try {
                saveNormalisasiFromPenilaian(penilaian.getId());
                count++;
            } catch (Exception e) {
                // Log error but continue processing
                System.err.println("Error generating normalisasi for penilaian ID " + penilaian.getId() + ": " + e.getMessage());
            }
        }

        return count;
    }


    public void deleteNormalisasi(Long id) {
        Optional<Normalisasi> normalisasi = normalisasiRepository.findById(id);

        if (!normalisasi.isPresent()) {
            throw new IllegalArgumentException("Normalisasi tidak ditemukan");
        }

        normalisasi.get().setStatusAktif(false);
        normalisasiRepository.save(normalisasi.get());
    }


    public void permanentDeleteNormalisasi(Long id) {
        Optional<Normalisasi> normalisasi = normalisasiRepository.findById(id);

        if (!normalisasi.isPresent()) {
            throw new IllegalArgumentException("Normalisasi tidak ditemukan");
        }

        normalisasiRepository.delete(normalisasi.get());
    }


    public void activateNormalisasi(Long id) {
        Optional<Normalisasi> normalisasi = normalisasiRepository.findById(id);

        if (!normalisasi.isPresent()) {
            throw new IllegalArgumentException("Normalisasi tidak ditemukan");
        }

        normalisasi.get().setStatusAktif(true);
        normalisasiRepository.save(normalisasi.get());
    }

    /**
     * Calculate nilai referensi from normalisasi * bobot kriteria
     * @return List of NilaiReferensiDTO
     */
    public List<NilaiReferensiDTO> calculateNilaiReferensi() {
        return normalisasiRepository.calculateNilaiReferensi();
    }

    /**
     * Calculate nilai referensi filtered by bulan and tahun
     * @param bulan Bulan penilaian
     * @param tahun Tahun penilaian
     * @return List of NilaiReferensiDTO
     */
    public List<NilaiReferensiDTO> calculateNilaiReferensiByBulanAndTahun(Integer bulan, Integer tahun) {
        return normalisasiRepository.calculateNilaiReferensiByBulanAndTahun(bulan, tahun);
    }

    /**
     * Calculate nilai referensi filtered by tahun only (no month filter)
     * @param tahun Tahun penilaian
     * @return List of NilaiReferensiDTO
     */
    public List<NilaiReferensiDTO> calculateNilaiReferensiByTahun(Integer tahun) {
        return normalisasiRepository.calculateNilaiReferensiByTahun(tahun);
    }


    public void deactivateNormalisasi(Long id) {
        Optional<Normalisasi> normalisasi = normalisasiRepository.findById(id);

        if (!normalisasi.isPresent()) {
            throw new IllegalArgumentException("Normalisasi tidak ditemukan");
        }

        normalisasi.get().setStatusAktif(false);
        normalisasiRepository.save(normalisasi.get());
    }


    public Double getAverageTotalNormalisasiByKaryawan(Long karyawanId) {
        return normalisasiRepository.getAverageTotalNormalisasiByKaryawan(karyawanId);
    }
}

