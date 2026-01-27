package id.co.lua.pbj.penilaian_karyawan.model.repositories.apps;

import id.co.lua.pbj.penilaian_karyawan.model.apps.DetailPenilaian;
import id.co.lua.pbj.penilaian_karyawan.model.apps.PenilaianKaryawan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailPenilaianRepository extends JpaRepository<DetailPenilaian, Long> {

    /**
     * Find all detail penilaian by penilaian karyawan
     */
    List<DetailPenilaian> findByPenilaianKaryawan(PenilaianKaryawan penilaianKaryawan);

    /**
     * Find all detail penilaian by penilaian karyawan id
     */
    @Query("SELECT dp FROM DetailPenilaian dp WHERE dp.penilaianKaryawan.id = :penilaianId")
    List<DetailPenilaian> findByPenilaianKaryawanId(@Param("penilaianId") Long penilaianId);

    /**
     * Find all active detail penilaian by penilaian karyawan id
     */
    @Query("SELECT dp FROM DetailPenilaian dp WHERE dp.penilaianKaryawan.id = :penilaianId AND dp.statusAktif = true")
    List<DetailPenilaian> findActiveByPenilaianKaryawanId(@Param("penilaianId") Long penilaianId);

    /**
     * Delete all detail by penilaian karyawan id
     */
    void deleteByPenilaianKaryawanId(Long penilaianId);
}

