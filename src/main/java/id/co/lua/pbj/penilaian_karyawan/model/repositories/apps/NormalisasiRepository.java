package id.co.lua.pbj.penilaian_karyawan.model.repositories.apps;

import id.co.lua.pbj.penilaian_karyawan.model.apps.Normalisasi;
import id.co.lua.pbj.penilaian_karyawan.model.dto.NilaiReferensiDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NormalisasiRepository extends JpaRepository<Normalisasi, Long> {


    List<Normalisasi> findByStatusAktifTrueOrderByTahunDescBulanDesc();


    List<Normalisasi> findByKaryawanIdAndStatusAktifTrueOrderByTahunDescBulanDesc(Long karyawanId);


    List<Normalisasi> findByTahunAndStatusAktifTrueOrderByBulanDesc(Integer tahun);


    List<Normalisasi> findByBulanAndTahunAndStatusAktifTrue(Integer bulan, Integer tahun);


    Optional<Normalisasi> findByPenilaianKaryawanIdAndStatusAktifTrue(Long penilaianKaryawanId);


    @Query("SELECT n FROM Normalisasi n " +
           "WHERE n.statusAktif = true " +
           "AND (LOWER(n.karyawan.namaKaryawan) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(n.divisi.namaDivisi) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(n.jabatan.namaJabatan) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY n.tahun DESC, n.bulan DESC")
    List<Normalisasi> searchNormalisasi(@Param("keyword") String keyword);


    boolean existsByPenilaianKaryawanIdAndStatusAktifTrue(Long penilaianKaryawanId);

    Optional<Normalisasi> findByPenilaianKaryawanId(Long penilaianKaryawanId);


    @Query("SELECT AVG(n.totalNormalisasi) FROM Normalisasi n " +
           "WHERE n.karyawan.id = :karyawanId AND n.statusAktif = true")
    Double getAverageTotalNormalisasiByKaryawan(@Param("karyawanId") Long karyawanId);

    /**
     * Get nilai referensi by calculating normalisasi * bobot
     * Formula: (k1_normalisasi * bobot_k1) + (k2_normalisasi * bobot_k2) + ... + (k5_normalisasi * bobot_k5)
     */
    @Query("SELECT new id.co.lua.pbj.penilaian_karyawan.model.dto.NilaiReferensiDTO(" +
           "n.id, " +
           "n.karyawan.id, " +
           "n.karyawan.namaKaryawan, " +
           "n.divisi.namaDivisi, " +
           "n.jabatan.namaJabatan, " +
           "n.bulan, " +
           "n.tahun, " +
           "n.k1Normalisasi, " +
           "n.k2Normalisasi, " +
           "n.k3Normalisasi, " +
           "n.k4Normalisasi, " +
           "n.k5Normalisasi, " +
           "k1.bobot, " +
           "k2.bobot, " +
           "k3.bobot, " +
           "k4.bobot, " +
           "k5.bobot, " +
           "(COALESCE(n.k1Normalisasi, 0) * COALESCE(k1.bobot, 0)), " +
           "(COALESCE(n.k2Normalisasi, 0) * COALESCE(k2.bobot, 0)), " +
           "(COALESCE(n.k3Normalisasi, 0) * COALESCE(k3.bobot, 0)), " +
           "(COALESCE(n.k4Normalisasi, 0) * COALESCE(k4.bobot, 0)), " +
           "(COALESCE(n.k5Normalisasi, 0) * COALESCE(k5.bobot, 0)), " +
           "(COALESCE(n.k1Normalisasi, 0) * COALESCE(k1.bobot, 0) + " +
           " COALESCE(n.k2Normalisasi, 0) * COALESCE(k2.bobot, 0) + " +
           " COALESCE(n.k3Normalisasi, 0) * COALESCE(k3.bobot, 0) + " +
           " COALESCE(n.k4Normalisasi, 0) * COALESCE(k4.bobot, 0) + " +
           " COALESCE(n.k5Normalisasi, 0) * COALESCE(k5.bobot, 0))) " +
           "FROM Normalisasi n " +
           "LEFT JOIN KriteriaPenilaian k1 ON k1.kodeKriteria = 'K-001' AND k1.statusAktif = true " +
           "LEFT JOIN KriteriaPenilaian k2 ON k2.kodeKriteria = 'K-002' AND k2.statusAktif = true " +
           "LEFT JOIN KriteriaPenilaian k3 ON k3.kodeKriteria = 'K-003' AND k3.statusAktif = true " +
           "LEFT JOIN KriteriaPenilaian k4 ON k4.kodeKriteria = 'K-004' AND k4.statusAktif = true " +
           "LEFT JOIN KriteriaPenilaian k5 ON k5.kodeKriteria = 'K-005' AND k5.statusAktif = true " +
           "WHERE n.statusAktif = true " +
           "ORDER BY n.tahun DESC, n.bulan DESC")
    List<NilaiReferensiDTO> calculateNilaiReferensi();

    /**
     * Get nilai referensi filtered by bulan and tahun
     */
    @Query("SELECT new id.co.lua.pbj.penilaian_karyawan.model.dto.NilaiReferensiDTO(" +
           "n.id, " +
           "n.karyawan.id, " +
           "n.karyawan.namaKaryawan, " +
           "n.divisi.namaDivisi, " +
           "n.jabatan.namaJabatan, " +
           "n.bulan, " +
           "n.tahun, " +
           "n.k1Normalisasi, " +
           "n.k2Normalisasi, " +
           "n.k3Normalisasi, " +
           "n.k4Normalisasi, " +
           "n.k5Normalisasi, " +
           "k1.bobot, " +
           "k2.bobot, " +
           "k3.bobot, " +
           "k4.bobot, " +
           "k5.bobot, " +
           "(COALESCE(n.k1Normalisasi, 0) * COALESCE(k1.bobot, 0)), " +
           "(COALESCE(n.k2Normalisasi, 0) * COALESCE(k2.bobot, 0)), " +
           "(COALESCE(n.k3Normalisasi, 0) * COALESCE(k3.bobot, 0)), " +
           "(COALESCE(n.k4Normalisasi, 0) * COALESCE(k4.bobot, 0)), " +
           "(COALESCE(n.k5Normalisasi, 0) * COALESCE(k5.bobot, 0)), " +
           "(COALESCE(n.k1Normalisasi, 0) * COALESCE(k1.bobot, 0) + " +
           " COALESCE(n.k2Normalisasi, 0) * COALESCE(k2.bobot, 0) + " +
           " COALESCE(n.k3Normalisasi, 0) * COALESCE(k3.bobot, 0) + " +
           " COALESCE(n.k4Normalisasi, 0) * COALESCE(k4.bobot, 0) + " +
           " COALESCE(n.k5Normalisasi, 0) * COALESCE(k5.bobot, 0))) " +
           "FROM Normalisasi n " +
           "LEFT JOIN KriteriaPenilaian k1 ON k1.kodeKriteria = 'K-001' AND k1.statusAktif = true " +
           "LEFT JOIN KriteriaPenilaian k2 ON k2.kodeKriteria = 'K-002' AND k2.statusAktif = true " +
           "LEFT JOIN KriteriaPenilaian k3 ON k3.kodeKriteria = 'K-003' AND k3.statusAktif = true " +
           "LEFT JOIN KriteriaPenilaian k4 ON k4.kodeKriteria = 'K-004' AND k4.statusAktif = true " +
           "LEFT JOIN KriteriaPenilaian k5 ON k5.kodeKriteria = 'K-005' AND k5.statusAktif = true " +
           "WHERE n.bulan = :bulan AND n.tahun = :tahun AND n.statusAktif = true " +
           "ORDER BY (COALESCE(n.k1Normalisasi, 0) * COALESCE(k1.bobot, 0) + " +
           " COALESCE(n.k2Normalisasi, 0) * COALESCE(k2.bobot, 0) + " +
           " COALESCE(n.k3Normalisasi, 0) * COALESCE(k3.bobot, 0) + " +
           " COALESCE(n.k4Normalisasi, 0) * COALESCE(k4.bobot, 0) + " +
           " COALESCE(n.k5Normalisasi, 0) * COALESCE(k5.bobot, 0)) DESC")
    List<NilaiReferensiDTO> calculateNilaiReferensiByBulanAndTahun(@Param("bulan") Integer bulan, @Param("tahun") Integer tahun);

    /**
     * Get nilai referensi by calculating normalisasi * bobot - filtered by year only
     * Formula: (k1_normalisasi * bobot_k1) + (k2_normalisasi * bobot_k2) + ... + (k5_normalisasi * bobot_k5)
     */
    @Query("SELECT new id.co.lua.pbj.penilaian_karyawan.model.dto.NilaiReferensiDTO(" +
           "n.id, " +
           "n.karyawan.id, " +
           "n.karyawan.namaKaryawan, " +
           "n.divisi.namaDivisi, " +
           "n.jabatan.namaJabatan, " +
           "n.bulan, " +
           "n.tahun, " +
           "n.k1Normalisasi, " +
           "n.k2Normalisasi, " +
           "n.k3Normalisasi, " +
           "n.k4Normalisasi, " +
           "n.k5Normalisasi, " +
           "k1.bobot, " +
           "k2.bobot, " +
           "k3.bobot, " +
           "k4.bobot, " +
           "k5.bobot, " +
           "(COALESCE(n.k1Normalisasi, 0) * COALESCE(k1.bobot, 0)), " +
           "(COALESCE(n.k2Normalisasi, 0) * COALESCE(k2.bobot, 0)), " +
           "(COALESCE(n.k3Normalisasi, 0) * COALESCE(k3.bobot, 0)), " +
           "(COALESCE(n.k4Normalisasi, 0) * COALESCE(k4.bobot, 0)), " +
           "(COALESCE(n.k5Normalisasi, 0) * COALESCE(k5.bobot, 0)), " +
           "(COALESCE(n.k1Normalisasi, 0) * COALESCE(k1.bobot, 0) + " +
           " COALESCE(n.k2Normalisasi, 0) * COALESCE(k2.bobot, 0) + " +
           " COALESCE(n.k3Normalisasi, 0) * COALESCE(k3.bobot, 0) + " +
           " COALESCE(n.k4Normalisasi, 0) * COALESCE(k4.bobot, 0) + " +
           " COALESCE(n.k5Normalisasi, 0) * COALESCE(k5.bobot, 0))) " +
           "FROM Normalisasi n " +
           "LEFT JOIN KriteriaPenilaian k1 ON k1.kodeKriteria = 'K-001' AND k1.statusAktif = true " +
           "LEFT JOIN KriteriaPenilaian k2 ON k2.kodeKriteria = 'K-002' AND k2.statusAktif = true " +
           "LEFT JOIN KriteriaPenilaian k3 ON k3.kodeKriteria = 'K-003' AND k3.statusAktif = true " +
           "LEFT JOIN KriteriaPenilaian k4 ON k4.kodeKriteria = 'K-004' AND k4.statusAktif = true " +
           "LEFT JOIN KriteriaPenilaian k5 ON k5.kodeKriteria = 'K-005' AND k5.statusAktif = true " +
           "WHERE n.tahun = :tahun AND n.statusAktif = true " +
           "ORDER BY (COALESCE(n.k1Normalisasi, 0) * COALESCE(k1.bobot, 0) + " +
           " COALESCE(n.k2Normalisasi, 0) * COALESCE(k2.bobot, 0) + " +
           " COALESCE(n.k3Normalisasi, 0) * COALESCE(k3.bobot, 0) + " +
           " COALESCE(n.k4Normalisasi, 0) * COALESCE(k4.bobot, 0) + " +
           " COALESCE(n.k5Normalisasi, 0) * COALESCE(k5.bobot, 0)) DESC")
    List<NilaiReferensiDTO> calculateNilaiReferensiByTahun(@Param("tahun") Integer tahun);
}

