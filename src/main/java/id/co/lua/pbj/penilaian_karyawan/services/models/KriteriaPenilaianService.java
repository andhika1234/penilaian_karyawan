package id.co.lua.pbj.penilaian_karyawan.services.models;

import id.co.lua.pbj.penilaian_karyawan.model.apps.KriteriaPenilaian;

import java.util.List;
import java.util.Optional;

public interface KriteriaPenilaianService {

    /**
     * Get all kriteria
     * @return List of all kriteria
     */
    List<KriteriaPenilaian> getAllKriteria();

    /**
     * Get all kriteria ordered by kode
     * @return List of all kriteria ordered by kode
     */
    List<KriteriaPenilaian> getAllKriteriaOrderByKode();

    /**
     * Get all active kriteria
     * @return List of active kriteria
     */
    List<KriteriaPenilaian> getAllActiveKriteria();

    /**
     * Get kriteria by ID
     * @param id Kriteria ID
     * @return Optional KriteriaPenilaian
     */
    Optional<KriteriaPenilaian> getKriteriaById(Long id);

    /**
     * Get kriteria by kode kriteria
     * @param kodeKriteria Kode Kriteria
     * @return Optional KriteriaPenilaian
     */
    Optional<KriteriaPenilaian> getKriteriaByKodeKriteria(String kodeKriteria);

    /**
     * Get kriteria by nama kriteria
     * @param namaKriteria Nama Kriteria
     * @return Optional KriteriaPenilaian
     */
    Optional<KriteriaPenilaian> getKriteriaByNamaKriteria(String namaKriteria);

    /**
     * Search kriteria by keyword
     * @param keyword Search keyword
     * @return List of kriteria matching the keyword
     */
    List<KriteriaPenilaian> searchKriteria(String keyword);

    /**
     * Save new kriteria
     * @param kriteria KriteriaPenilaian object
     * @return Saved kriteria
     */
    KriteriaPenilaian saveKriteria(KriteriaPenilaian kriteria);

    /**
     * Update existing kriteria
     * @param id Kriteria ID
     * @param kriteria Updated kriteria data
     * @return Updated kriteria
     */
    KriteriaPenilaian updateKriteria(Long id, KriteriaPenilaian kriteria);

    /**
     * Delete kriteria (soft delete)
     * @param id Kriteria ID
     */
    void deleteKriteria(Long id);

    /**
     * Permanently delete kriteria
     * @param id Kriteria ID
     */
    void permanentDeleteKriteria(Long id);

    /**
     * Activate kriteria
     * @param id Kriteria ID
     */
    void activateKriteria(Long id);

    /**
     * Deactivate kriteria
     * @param id Kriteria ID
     */
    void deactivateKriteria(Long id);

    /**
     * Check if kode kriteria exists
     * @param kodeKriteria Kode Kriteria to check
     * @return true if exists
     */
    boolean isKodeKriteriaExists(String kodeKriteria);

    /**
     * Check if nama kriteria exists
     * @param namaKriteria Nama Kriteria to check
     * @return true if exists
     */
    boolean isNamaKriteriaExists(String namaKriteria);

    /**
     * Check if kode kriteria exists excluding current ID
     * @param kodeKriteria Kode Kriteria to check
     * @param id ID to exclude
     * @return true if exists
     */
    boolean isKodeKriteriaExistsExcludingId(String kodeKriteria, Long id);

    /**
     * Check if nama kriteria exists excluding current ID
     * @param namaKriteria Nama Kriteria to check
     * @param id ID to exclude
     * @return true if exists
     */
    boolean isNamaKriteriaExistsExcludingId(String namaKriteria, Long id);
}

