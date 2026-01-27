package id.co.lua.pbj.penilaian_karyawan.model.repositories.apps;

import id.co.lua.pbj.penilaian_karyawan.model.apps.Divisi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DivisiRepository extends JpaRepository<Divisi, Long> {

    Optional<Divisi> findByKodeDivisi(String kodeDivisi);

    Optional<Divisi> findByNamaDivisi(String namaDivisi);

    List<Divisi> findByStatusAktif(Boolean statusAktif);

    @Query("SELECT d FROM Divisi d WHERE d.statusAktif = true ORDER BY d.namaDivisi ASC")
    List<Divisi> findAllActiveDivisi();

    @Query("SELECT d FROM Divisi d ORDER BY d.namaDivisi ASC")
    List<Divisi> findAllOrderByNama();

    @Query("SELECT d FROM Divisi d WHERE " +
           "LOWER(d.kodeDivisi) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.namaDivisi) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Divisi> searchDivisi(@Param("keyword") String keyword);

    @Query("SELECT COUNT(d) FROM Divisi d WHERE d.kodeDivisi = :kodeDivisi")
    int countByKodeDivisi(@Param("kodeDivisi") String kodeDivisi);

    @Query("SELECT COUNT(d) FROM Divisi d WHERE d.namaDivisi = :namaDivisi")
    int countByNamaDivisi(@Param("namaDivisi") String namaDivisi);

    @Query("SELECT COUNT(d) FROM Divisi d WHERE d.kodeDivisi = :kodeDivisi AND d.id <> :id")
    int countByKodeDivisiExcludingId(@Param("kodeDivisi") String kodeDivisi, @Param("id") Long id);

    @Query("SELECT COUNT(d) FROM Divisi d WHERE d.namaDivisi = :namaDivisi AND d.id <> :id")
    int countByNamaDivisiExcludingId(@Param("namaDivisi") String namaDivisi, @Param("id") Long id);
}

