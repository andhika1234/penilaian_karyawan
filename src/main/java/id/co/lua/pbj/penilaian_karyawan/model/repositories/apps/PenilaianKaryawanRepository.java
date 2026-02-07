package id.co.lua.pbj.penilaian_karyawan.model.repositories.apps;

import id.co.lua.pbj.penilaian_karyawan.model.apps.Karyawan;
import id.co.lua.pbj.penilaian_karyawan.model.apps.PenilaianKaryawan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PenilaianKaryawanRepository extends JpaRepository<PenilaianKaryawan, Long> {

    List<PenilaianKaryawan> findByKaryawan(Karyawan karyawan);

    @Query("SELECT p FROM PenilaianKaryawan p " +
           "LEFT JOIN FETCH p.detailPenilaianList d " +
           "LEFT JOIN FETCH d.kriteriaPenilaian " +
           "LEFT JOIN FETCH p.karyawan " +
           "LEFT JOIN FETCH p.divisi " +
           "LEFT JOIN FETCH p.jabatan " +
           "WHERE p.bulan = :bulan AND p.tahun = :tahun " +
           "ORDER BY p.karyawan.namaKaryawan ASC")
    List<PenilaianKaryawan> findByBulanAndTahun(@Param("bulan") Integer bulan, @Param("tahun") Integer tahun);

    List<PenilaianKaryawan> findByTahun(Integer tahun);

    List<PenilaianKaryawan> findByStatusAktif(Boolean statusAktif);

    @Query("SELECT p FROM PenilaianKaryawan p " +
           "LEFT JOIN FETCH p.detailPenilaianList d " +
           "LEFT JOIN FETCH d.kriteriaPenilaian " +
           "LEFT JOIN FETCH p.karyawan " +
           "LEFT JOIN FETCH p.divisi " +
           "LEFT JOIN FETCH p.jabatan " +
           "WHERE p.statusAktif = true " +
           "ORDER BY p.tahun DESC, p.bulan DESC, p.karyawan.namaKaryawan ASC")
    List<PenilaianKaryawan> findAllActivePenilaian();

    @Query("SELECT p FROM PenilaianKaryawan p WHERE " +
           "p.karyawan.id = :karyawanId AND p.bulan = :bulan AND p.tahun = :tahun")
    Optional<PenilaianKaryawan> findByKaryawanAndBulanAndTahun(
        @Param("karyawanId") Long karyawanId,
        @Param("bulan") Integer bulan,
        @Param("tahun") Integer tahun
    );

    @Query("SELECT p FROM PenilaianKaryawan p WHERE " +
           "LOWER(p.karyawan.namaKaryawan) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.karyawan.nik) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.karyawan.divisi.namaDivisi) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.kategoriPenilaian) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<PenilaianKaryawan> searchPenilaian(@Param("keyword") String keyword);

    @Query("SELECT COUNT(p) FROM PenilaianKaryawan p WHERE " +
           "p.karyawan.id = :karyawanId AND p.bulan = :bulan AND p.tahun = :tahun")
    int countByKaryawanAndBulanAndTahun(
        @Param("karyawanId") Long karyawanId,
        @Param("bulan") Integer bulan,
        @Param("tahun") Integer tahun
    );

    @Query("SELECT COUNT(p) FROM PenilaianKaryawan p WHERE " +
           "p.karyawan.id = :karyawanId AND p.bulan = :bulan AND p.tahun = :tahun AND p.id <> :id")
    int countByKaryawanAndBulanAndTahunExcludingId(
        @Param("karyawanId") Long karyawanId,
        @Param("bulan") Integer bulan,
        @Param("tahun") Integer tahun,
        @Param("id") Long id
    );

    @Query("SELECT p FROM PenilaianKaryawan p WHERE p.karyawan.id = :karyawanId " +
           "ORDER BY p.tahun DESC, p.bulan DESC")
    List<PenilaianKaryawan> findByKaryawanIdOrderByTahunDesc(@Param("karyawanId") Long karyawanId);

    @Query("SELECT AVG(p.nilaiRataRata) FROM PenilaianKaryawan p WHERE p.karyawan.id = :karyawanId")
    Double getAverageNilaiByKaryawan(@Param("karyawanId") Long karyawanId);

    @Query("SELECT p FROM PenilaianKaryawan p WHERE p.tahun = :tahun " +
           "ORDER BY p.nilaiRataRata DESC, p.karyawan.namaKaryawan ASC")
    List<PenilaianKaryawan> findByTahunOrderByNilaiDesc(@Param("tahun") Integer tahun);

    @Query("SELECT p FROM PenilaianKaryawan p WHERE p.bulan = :bulan AND p.tahun = :tahun AND p.statusAktif = true " +
           "ORDER BY p.totalNilai DESC, p.nilaiRataRata DESC, p.karyawan.namaKaryawan ASC")
    List<PenilaianKaryawan> findByBulanAndTahunOrderByTotalNilaiDesc(
        @Param("bulan") Integer bulan,
        @Param("tahun") Integer tahun
    );

    @Query("SELECT p FROM PenilaianKaryawan p WHERE p.tahun = :tahun AND p.statusAktif = true " +
           "ORDER BY p.totalNilai DESC, p.nilaiRataRata DESC, p.karyawan.namaKaryawan ASC")
    List<PenilaianKaryawan> findByTahunOrderByTotalNilaiDesc(@Param("tahun") Integer tahun);

    @Query("SELECT p.karyawan.id as karyawanId, " +
           "p.karyawan.namaKaryawan as namaKaryawan, " +
           "p.divisi.namaDivisi as namaDivisi, " +
           "p.jabatan.namaJabatan as namaJabatan, " +
           "p.tahun as tahun, " +
           "SUM(p.totalNilai) as totalBobotTahunan, " +
           "AVG(p.nilaiRataRata) as rataRataTahunan, " +
           "COUNT(p.id) as jumlahBulan " +
           "FROM PenilaianKaryawan p " +
           "WHERE p.tahun = :tahun AND p.statusAktif = true " +
           "GROUP BY p.karyawan.id, p.karyawan.namaKaryawan, p.divisi.namaDivisi, p.jabatan.namaJabatan, p.tahun " +
           "ORDER BY SUM(p.totalNilai) DESC, AVG(p.nilaiRataRata) DESC, p.karyawan.namaKaryawan ASC")
    List<Object[]> getRekapTahunanByTahun(@Param("tahun") Integer tahun);

    @Query("SELECT p.karyawan.id as karyawanId, " +
           "p.karyawan.namaKaryawan as namaKaryawan, " +
           "p.divisi.namaDivisi as namaDivisi, " +
           "p.jabatan.namaJabatan as namaJabatan, " +
           "p.tahun as tahun, " +
           "SUM(p.totalNilai) as totalBobotTahunan, " +
           "AVG(p.nilaiRataRata) as rataRataTahunan, " +
           "COUNT(p.id) as jumlahBulan " +
           "FROM PenilaianKaryawan p " +
           "WHERE p.statusAktif = true " +
           "GROUP BY p.karyawan.id, p.karyawan.namaKaryawan, p.divisi.namaDivisi, p.jabatan.namaJabatan, p.tahun " +
           "ORDER BY p.tahun DESC, SUM(p.totalNilai) DESC, AVG(p.nilaiRataRata) DESC, p.karyawan.namaKaryawan ASC")
    List<Object[]> getAllRekapTahunan();
}

