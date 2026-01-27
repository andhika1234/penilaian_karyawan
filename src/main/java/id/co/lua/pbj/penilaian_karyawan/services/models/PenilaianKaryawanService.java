package id.co.lua.pbj.penilaian_karyawan.services.models;

import id.co.lua.pbj.penilaian_karyawan.model.apps.Karyawan;
import id.co.lua.pbj.penilaian_karyawan.model.apps.PenilaianKaryawan;
import id.co.lua.pbj.penilaian_karyawan.model.dto.RekapTahunanDTO;

import java.util.List;
import java.util.Optional;

public interface PenilaianKaryawanService {

    /**
     * Get all penilaian karyawan
     * @return List of all penilaian
     */
    List<PenilaianKaryawan> getAllPenilaian();

    /**
     * Get all active penilaian karyawan
     * @return List of active penilaian
     */
    List<PenilaianKaryawan> getAllActivePenilaian();

    /**
     * Get penilaian by ID
     * @param id Penilaian ID
     * @return Optional PenilaianKaryawan
     */
    Optional<PenilaianKaryawan> getPenilaianById(Long id);

    /**
     * Get penilaian by karyawan
     * @param karyawan Karyawan entity
     * @return List of penilaian for the karyawan
     */
    List<PenilaianKaryawan> getPenilaianByKaryawan(Karyawan karyawan);

    /**
     * Get penilaian by karyawan ID
     * @param karyawanId Karyawan ID
     * @return List of penilaian for the karyawan
     */
    List<PenilaianKaryawan> getPenilaianByKaryawanId(Long karyawanId);

    /**
     * Get penilaian by bulan and tahun
     * @param bulan Bulan
     * @param tahun Tahun
     * @return List of penilaian
     */
    List<PenilaianKaryawan> getPenilaianByBulanAndTahun(Integer bulan, Integer tahun);

    /**
     * Get penilaian by tahun
     * @param tahun Tahun
     * @return List of penilaian
     */
    List<PenilaianKaryawan> getPenilaianByTahun(Integer tahun);

    /**
     * Get penilaian by tahun ordered by nilai
     * @param tahun Tahun
     * @return List of penilaian sorted by nilai
     */
    List<PenilaianKaryawan> getPenilaianByTahunOrderByNilai(Integer tahun);

    /**
     * Search penilaian by keyword
     * @param keyword Search keyword
     * @return List of penilaian matching the keyword
     */
    List<PenilaianKaryawan> searchPenilaian(String keyword);

    /**
     * Save new penilaian
     * @param penilaian PenilaianKaryawan object
     * @return Saved penilaian
     */
    PenilaianKaryawan savePenilaian(PenilaianKaryawan penilaian);

    /**
     * Update existing penilaian
     * @param id Penilaian ID
     * @param penilaian Updated penilaian data
     * @return Updated penilaian
     */
    PenilaianKaryawan updatePenilaian(Long id, PenilaianKaryawan penilaian);

    /**
     * Delete penilaian (soft delete)
     * @param id Penilaian ID
     */
    void deletePenilaian(Long id);

    /**
     * Permanently delete penilaian
     * @param id Penilaian ID
     */
    void permanentDeletePenilaian(Long id);

    /**
     * Activate penilaian
     * @param id Penilaian ID
     */
    void activatePenilaian(Long id);

    /**
     * Deactivate penilaian
     * @param id Penilaian ID
     */
    void deactivatePenilaian(Long id);

    /**
     * Check if penilaian exists for karyawan in bulan and tahun
     * @param karyawanId Karyawan ID
     * @param bulan Bulan
     * @param tahun Tahun
     * @return true if exists
     */
    boolean isPenilaianExists(Long karyawanId, Integer bulan, Integer tahun);

    /**
     * Check if penilaian exists excluding current ID
     * @param karyawanId Karyawan ID
     * @param bulan Bulan
     * @param tahun Tahun
     * @param id ID to exclude
     * @return true if exists
     */
    boolean isPenilaianExistsExcludingId(Long karyawanId, Integer bulan, Integer tahun, Long id);

    /**
     * Get average nilai for karyawan
     * @param karyawanId Karyawan ID
     * @return Average nilai
     */
    Double getAverageNilaiByKaryawan(Long karyawanId);

    /**
     * Get peringkat penilaian by bulan and tahun ordered by total bobot (nilai)
     * @param bulan Bulan
     * @param tahun Tahun
     * @return List of penilaian ordered by total nilai descending
     */
    List<PenilaianKaryawan> getPeringkatByBulanAndTahun(Integer bulan, Integer tahun);

    /**
     * Get peringkat penilaian by tahun ordered by total bobot (nilai)
     * @param tahun Tahun
     * @return List of penilaian ordered by total nilai descending
     */
    List<PenilaianKaryawan> getPeringkatByTahun(Integer tahun);

    /**
     * Get rekap tahunan by tahun (sum of all months)
     * @param tahun Tahun
     * @return List of rekap tahunan ordered by total bobot descending
     */
    List<RekapTahunanDTO> getRekapTahunanByTahun(Integer tahun);

    /**
     * Get all rekap tahunan (sum of all months for all years)
     * @return List of rekap tahunan ordered by year and total bobot descending
     */
    List<RekapTahunanDTO> getAllRekapTahunan();
}

