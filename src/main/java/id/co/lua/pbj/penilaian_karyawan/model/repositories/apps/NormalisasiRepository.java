package id.co.lua.pbj.penilaian_karyawan.model.repositories.apps;

import id.co.lua.pbj.penilaian_karyawan.model.apps.Normalisasi;
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


    @Query("SELECT AVG(n.totalNormalisasi) FROM Normalisasi n " +
           "WHERE n.karyawan.id = :karyawanId AND n.statusAktif = true")
    Double getAverageTotalNormalisasiByKaryawan(@Param("karyawanId") Long karyawanId);
}

