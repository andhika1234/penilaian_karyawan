package id.co.lua.pbj.penilaian_karyawan.services.models;

import id.co.lua.pbj.penilaian_karyawan.model.apps.Jabatan;
import id.co.lua.pbj.penilaian_karyawan.model.apps.Karyawan;

import java.util.List;
import java.util.Optional;

public interface KaryawanService {

    /**
     * Get all karyawan
     * @return List of all karyawan
     */
    List<Karyawan> getAllKaryawan();

    /**
     * Get all active karyawan
     * @return List of active karyawan
     */
    List<Karyawan> getAllActiveKaryawan();

    /**
     * Get karyawan by ID
     * @param id Karyawan ID
     * @return Optional Karyawan
     */
    Optional<Karyawan> getKaryawanById(Long id);

    /**
     * Get karyawan by NIK
     * @param nik NIK
     * @return Optional Karyawan
     */
    Optional<Karyawan> getKaryawanByNik(String nik);

    /**
     * Get karyawan by email
     * @param email Email
     * @return Optional Karyawan
     */
    Optional<Karyawan> getKaryawanByEmail(String email);

    /**
     * Get karyawan by jabatan
     * @param jabatan Jabatan entity
     * @return List of karyawan with the position
     */
    List<Karyawan> getKaryawanByJabatan(Jabatan jabatan);

    /**
     * Search karyawan by keyword
     * @param keyword Search keyword
     * @return List of karyawan matching the keyword
     */
    List<Karyawan> searchKaryawan(String keyword);

    /**
     * Save new karyawan
     * @param karyawan Karyawan object
     * @return Saved karyawan
     */
    Karyawan saveKaryawan(Karyawan karyawan);

    /**
     * Update existing karyawan
     * @param id Karyawan ID
     * @param karyawan Updated karyawan data
     * @return Updated karyawan
     */
    Karyawan updateKaryawan(Long id, Karyawan karyawan);

    /**
     * Delete karyawan (soft delete)
     * @param id Karyawan ID
     */
    void deleteKaryawan(Long id);

    /**
     * Permanently delete karyawan
     * @param id Karyawan ID
     */
    void permanentDeleteKaryawan(Long id);

    /**
     * Activate karyawan
     * @param id Karyawan ID
     */
    void activateKaryawan(Long id);

    /**
     * Deactivate karyawan
     * @param id Karyawan ID
     */
    void deactivateKaryawan(Long id);

    /**
     * Check if NIK exists
     * @param nik NIK to check
     * @return true if exists
     */
    boolean isNikExists(String nik);

    /**
     * Check if email exists
     * @param email Email to check
     * @return true if exists
     */
    boolean isEmailExists(String email);

    /**
     * Check if NIK exists excluding current ID
     * @param nik NIK to check
     * @param id ID to exclude
     * @return true if exists
     */
    boolean isNikExistsExcludingId(String nik, Long id);

    /**
     * Check if email exists excluding current ID
     * @param email Email to check
     * @param id ID to exclude
     * @return true if exists
     */
    boolean isEmailExistsExcludingId(String email, Long id);
}

