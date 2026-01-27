package id.co.lua.pbj.penilaian_karyawan.services.models;

import id.co.lua.pbj.penilaian_karyawan.model.apps.Jabatan;

import java.util.List;
import java.util.Optional;

public interface JabatanService {

    /**
     * Get all jabatan
     * @return List of all jabatan
     */
    List<Jabatan> getAllJabatan();

    /**
     * Get all jabatan ordered by name
     * @return List of all jabatan ordered by name
     */
    List<Jabatan> getAllJabatanOrderByNama();

    /**
     * Get all active jabatan
     * @return List of active jabatan
     */
    List<Jabatan> getAllActiveJabatan();

    /**
     * Get jabatan by ID
     * @param id Jabatan ID
     * @return Optional Jabatan
     */
    Optional<Jabatan> getJabatanById(Long id);

    /**
     * Get jabatan by kode jabatan
     * @param kodeJabatan Kode Jabatan
     * @return Optional Jabatan
     */
    Optional<Jabatan> getJabatanByKodeJabatan(String kodeJabatan);

    /**
     * Get jabatan by nama jabatan
     * @param namaJabatan Nama Jabatan
     * @return Optional Jabatan
     */
    Optional<Jabatan> getJabatanByNamaJabatan(String namaJabatan);

    /**
     * Search jabatan by keyword
     * @param keyword Search keyword
     * @return List of jabatan matching the keyword
     */
    List<Jabatan> searchJabatan(String keyword);

    /**
     * Save new jabatan
     * @param jabatan Jabatan object
     * @return Saved jabatan
     */
    Jabatan saveJabatan(Jabatan jabatan);

    /**
     * Update existing jabatan
     * @param id Jabatan ID
     * @param jabatan Updated jabatan data
     * @return Updated jabatan
     */
    Jabatan updateJabatan(Long id, Jabatan jabatan);

    /**
     * Delete jabatan (soft delete)
     * @param id Jabatan ID
     */
    void deleteJabatan(Long id);

    /**
     * Permanently delete jabatan
     * @param id Jabatan ID
     */
    void permanentDeleteJabatan(Long id);

    /**
     * Activate jabatan
     * @param id Jabatan ID
     */
    void activateJabatan(Long id);

    /**
     * Deactivate jabatan
     * @param id Jabatan ID
     */
    void deactivateJabatan(Long id);

    /**
     * Check if kode jabatan exists
     * @param kodeJabatan Kode Jabatan to check
     * @return true if exists
     */
    boolean isKodeJabatanExists(String kodeJabatan);

    /**
     * Check if nama jabatan exists
     * @param namaJabatan Nama Jabatan to check
     * @return true if exists
     */
    boolean isNamaJabatanExists(String namaJabatan);

    /**
     * Check if kode jabatan exists excluding specific ID
     * @param kodeJabatan Kode Jabatan to check
     * @param id ID to exclude
     * @return true if exists
     */
    boolean isKodeJabatanExistsExcludingId(String kodeJabatan, Long id);

    /**
     * Check if nama jabatan exists excluding specific ID
     * @param namaJabatan Nama Jabatan to check
     * @param id ID to exclude
     * @return true if exists
     */
    boolean isNamaJabatanExistsExcludingId(String namaJabatan, Long id);
}

