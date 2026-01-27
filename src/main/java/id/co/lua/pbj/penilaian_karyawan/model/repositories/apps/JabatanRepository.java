package id.co.lua.pbj.penilaian_karyawan.model.repositories.apps;

import id.co.lua.pbj.penilaian_karyawan.model.apps.Jabatan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JabatanRepository extends JpaRepository<Jabatan, Long> {

    Optional<Jabatan> findByKodeJabatan(String kodeJabatan);

    Optional<Jabatan> findByNamaJabatan(String namaJabatan);

    List<Jabatan> findByStatusAktif(Boolean statusAktif);

    @Query("SELECT j FROM Jabatan j WHERE j.statusAktif = true ORDER BY j.namaJabatan ASC")
    List<Jabatan> findAllActiveJabatan();

    @Query("SELECT j FROM Jabatan j ORDER BY j.namaJabatan ASC")
    List<Jabatan> findAllOrderByNama();

    @Query("SELECT j FROM Jabatan j WHERE " +
           "LOWER(j.kodeJabatan) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.namaJabatan) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Jabatan> searchJabatan(@Param("keyword") String keyword);

    @Query("SELECT COUNT(j) FROM Jabatan j WHERE j.kodeJabatan = :kodeJabatan")
    int countByKodeJabatan(@Param("kodeJabatan") String kodeJabatan);

    @Query("SELECT COUNT(j) FROM Jabatan j WHERE j.namaJabatan = :namaJabatan")
    int countByNamaJabatan(@Param("namaJabatan") String namaJabatan);

    @Query("SELECT COUNT(j) FROM Jabatan j WHERE j.kodeJabatan = :kodeJabatan AND j.id <> :id")
    int countByKodeJabatanExcludingId(@Param("kodeJabatan") String kodeJabatan, @Param("id") Long id);

    @Query("SELECT COUNT(j) FROM Jabatan j WHERE j.namaJabatan = :namaJabatan AND j.id <> :id")
    int countByNamaJabatanExcludingId(@Param("namaJabatan") String namaJabatan, @Param("id") Long id);
}

