package id.co.lua.pbj.penilaian_karyawan.model.repositories.apps;

import id.co.lua.pbj.penilaian_karyawan.model.apps.Jabatan;
import id.co.lua.pbj.penilaian_karyawan.model.apps.Karyawan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KaryawanRepository extends JpaRepository<Karyawan, Long> {

    Optional<Karyawan> findByNik(String nik);

    Optional<Karyawan> findByEmailKaryawan(String emailKaryawan);

    List<Karyawan> findByJabatan(Jabatan jabatan);

    List<Karyawan> findByStatusAktif(Boolean statusAktif);

    @Query("SELECT k FROM Karyawan k WHERE k.statusAktif = true ORDER BY k.namaKaryawan ASC")
    List<Karyawan> findAllActiveKaryawan();

    @Query("SELECT k FROM Karyawan k WHERE " +
           "LOWER(k.namaKaryawan) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(k.nik) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(k.emailKaryawan) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(k.divisi.namaDivisi) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(k.jabatan.namaJabatan) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Karyawan> searchKaryawan(@Param("keyword") String keyword);

    @Query("SELECT COUNT(k) FROM Karyawan k WHERE k.nik = :nik")
    int countByNik(@Param("nik") String nik);

    @Query("SELECT COUNT(k) FROM Karyawan k WHERE k.emailKaryawan = :email")
    int countByEmail(@Param("email") String email);

    @Query("SELECT COUNT(k) FROM Karyawan k WHERE k.nik = :nik AND k.id <> :id")
    int countByNikExcludingId(@Param("nik") String nik, @Param("id") Long id);

    @Query("SELECT COUNT(k) FROM Karyawan k WHERE k.emailKaryawan = :email AND k.id <> :id")
    int countByEmailExcludingId(@Param("email") String email, @Param("id") Long id);
}

