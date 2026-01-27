package id.co.lua.pbj.penilaian_karyawan.services.models;

import id.co.lua.pbj.penilaian_karyawan.model.apps.Divisi;

import java.util.List;
import java.util.Optional;

public interface DivisiService {

    /**
     * Get all divisi
     * @return List of all divisi
     */
    List<Divisi> getAllDivisi();

    /**
     * Get all divisi ordered by name
     * @return List of all divisi ordered by name
     */
    List<Divisi> getAllDivisiOrderByNama();

    /**
     * Get all active divisi
     * @return List of active divisi
     */
    List<Divisi> getAllActiveDivisi();

    /**
     * Get divisi by ID
     * @param id Divisi ID
     * @return Optional Divisi
     */
    Optional<Divisi> getDivisiById(Long id);

    /**
     * Get divisi by kode divisi
     * @param kodeDivisi Kode Divisi
     * @return Optional Divisi
     */
    Optional<Divisi> getDivisiByKodeDivisi(String kodeDivisi);

    /**
     * Get divisi by nama divisi
     * @param namaDivisi Nama Divisi
     * @return Optional Divisi
     */
    Optional<Divisi> getDivisiByNamaDivisi(String namaDivisi);

    /**
     * Search divisi by keyword
     * @param keyword Search keyword
     * @return List of divisi matching the keyword
     */
    List<Divisi> searchDivisi(String keyword);

    /**
     * Save new divisi
     * @param divisi Divisi object
     * @return Saved divisi
     */
    Divisi saveDivisi(Divisi divisi);

    /**
     * Update existing divisi
     * @param id Divisi ID
     * @param divisi Updated divisi data
     * @return Updated divisi
     */
    Divisi updateDivisi(Long id, Divisi divisi);

    /**
     * Delete divisi (soft delete)
     * @param id Divisi ID
     */
    void deleteDivisi(Long id);

    /**
     * Permanently delete divisi
     * @param id Divisi ID
     */
    void permanentDeleteDivisi(Long id);

    /**
     * Activate divisi
     * @param id Divisi ID
     */
    void activateDivisi(Long id);

    /**
     * Deactivate divisi
     * @param id Divisi ID
     */
    void deactivateDivisi(Long id);

    /**
     * Check if kode divisi exists
     * @param kodeDivisi Kode Divisi to check
     * @return true if exists
     */
    boolean isKodeDivisiExists(String kodeDivisi);

    /**
     * Check if nama divisi exists
     * @param namaDivisi Nama Divisi to check
     * @return true if exists
     */
    boolean isNamaDivisiExists(String namaDivisi);

    /**
     * Check if kode divisi exists excluding current ID
     * @param kodeDivisi Kode Divisi to check
     * @param id ID to exclude
     * @return true if exists
     */
    boolean isKodeDivisiExistsExcludingId(String kodeDivisi, Long id);

    /**
     * Check if nama divisi exists excluding current ID
     * @param namaDivisi Nama Divisi to check
     * @param id ID to exclude
     * @return true if exists
     */
    boolean isNamaDivisiExistsExcludingId(String namaDivisi, Long id);
}

