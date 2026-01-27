package id.co.lua.pbj.penilaian_karyawan.model.repositories.apps;

import id.co.lua.pbj.penilaian_karyawan.model.apps.KriteriaPenilaian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KriteriaPenilaianRepository extends JpaRepository<KriteriaPenilaian, Long> {

    Optional<KriteriaPenilaian> findByKodeKriteria(String kodeKriteria);

    Optional<KriteriaPenilaian> findByNamaKriteria(String namaKriteria);

    List<KriteriaPenilaian> findByStatusAktif(Boolean statusAktif);

    @Query("SELECT k FROM KriteriaPenilaian k WHERE k.statusAktif = true ORDER BY k.kodeKriteria ASC")
    List<KriteriaPenilaian> findAllActiveKriteria();

    @Query("SELECT k FROM KriteriaPenilaian k ORDER BY k.kodeKriteria ASC")
    List<KriteriaPenilaian> findAllOrderByKode();

    @Query("SELECT k FROM KriteriaPenilaian k WHERE " +
           "LOWER(k.kodeKriteria) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(k.namaKriteria) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<KriteriaPenilaian> searchKriteria(@Param("keyword") String keyword);

    @Query("SELECT COUNT(k) FROM KriteriaPenilaian k WHERE k.kodeKriteria = :kodeKriteria")
    int countByKodeKriteria(@Param("kodeKriteria") String kodeKriteria);

    @Query("SELECT COUNT(k) FROM KriteriaPenilaian k WHERE k.namaKriteria = :namaKriteria")
    int countByNamaKriteria(@Param("namaKriteria") String namaKriteria);

    @Query("SELECT COUNT(k) FROM KriteriaPenilaian k WHERE k.kodeKriteria = :kodeKriteria AND k.id <> :id")
    int countByKodeKriteriaExcludingId(@Param("kodeKriteria") String kodeKriteria, @Param("id") Long id);

    @Query("SELECT COUNT(k) FROM KriteriaPenilaian k WHERE k.namaKriteria = :namaKriteria AND k.id <> :id")
    int countByNamaKriteriaExcludingId(@Param("namaKriteria") String namaKriteria, @Param("id") Long id);
}

