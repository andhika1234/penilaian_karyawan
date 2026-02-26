package id.co.lua.pbj.penilaian_karyawan.services.models;

import id.co.lua.pbj.penilaian_karyawan.model.apps.KriteriaPenilaian;
import id.co.lua.pbj.penilaian_karyawan.model.repositories.apps.KriteriaPenilaianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class KriteriaPenilaianServiceImpl implements KriteriaPenilaianService {

    @Autowired
    private KriteriaPenilaianRepository kriteriaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<KriteriaPenilaian> getAllKriteria() {
        return kriteriaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<KriteriaPenilaian> getAllKriteriaOrderByKode() {
        return kriteriaRepository.findAllOrderByKode();
    }

    @Override
    @Transactional(readOnly = true)
    public List<KriteriaPenilaian> getAllActiveKriteria() {
        return kriteriaRepository.findAllActiveKriteria();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KriteriaPenilaian> getKriteriaById(Long id) {
        return kriteriaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KriteriaPenilaian> getKriteriaByKodeKriteria(String kodeKriteria) {
        return kriteriaRepository.findByKodeKriteria(kodeKriteria);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KriteriaPenilaian> getKriteriaByNamaKriteria(String namaKriteria) {
        return kriteriaRepository.findByNamaKriteria(namaKriteria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KriteriaPenilaian> searchKriteria(String keyword) {
        return kriteriaRepository.searchKriteria(keyword);
    }

    @Override
    public KriteriaPenilaian saveKriteria(KriteriaPenilaian kriteria) {
        // Check if there is a soft-deleted record with the same kode kriteria -> reactivate it
        Optional<KriteriaPenilaian> softDeletedByKode = kriteriaRepository.findSoftDeletedByKodeKriteria(kriteria.getKodeKriteria());
        if (softDeletedByKode.isPresent()) {
            KriteriaPenilaian existing = softDeletedByKode.get();
            existing.setNamaKriteria(kriteria.getNamaKriteria());
            existing.setBobot(kriteria.getBobot());
            existing.setStatusAktif(true);
            return kriteriaRepository.save(existing);
        }

        // Validate kode kriteria uniqueness (only against active records)
        if (isKodeKriteriaExists(kriteria.getKodeKriteria())) {
            throw new IllegalArgumentException("Kode kriteria sudah terdaftar");
        }

        // Check if there is a soft-deleted record with the same nama kriteria -> reactivate it
        Optional<KriteriaPenilaian> softDeletedByNama = kriteriaRepository.findSoftDeletedByNamaKriteria(kriteria.getNamaKriteria());
        if (softDeletedByNama.isPresent()) {
            KriteriaPenilaian existing = softDeletedByNama.get();
            existing.setKodeKriteria(kriteria.getKodeKriteria());
            existing.setBobot(kriteria.getBobot());
            existing.setStatusAktif(true);
            return kriteriaRepository.save(existing);
        }

        // Validate nama kriteria uniqueness (only against active records)
        if (isNamaKriteriaExists(kriteria.getNamaKriteria())) {
            throw new IllegalArgumentException("Nama kriteria sudah terdaftar");
        }

        // Set default status to active
        if (kriteria.getStatusAktif() == null) {
            kriteria.setStatusAktif(true);
        }

        return kriteriaRepository.save(kriteria);
    }

    @Override
    public KriteriaPenilaian updateKriteria(Long id, KriteriaPenilaian kriteria) {
        Optional<KriteriaPenilaian> existingKriteria = kriteriaRepository.findById(id);

        if (!existingKriteria.isPresent()) {
            throw new IllegalArgumentException("Kriteria dengan ID " + id + " tidak ditemukan");
        }

        // Validate kode kriteria uniqueness (excluding current record)
        if (isKodeKriteriaExistsExcludingId(kriteria.getKodeKriteria(), id)) {
            throw new IllegalArgumentException("Kode kriteria sudah terdaftar");
        }

        // Validate nama kriteria uniqueness (excluding current record)
        if (isNamaKriteriaExistsExcludingId(kriteria.getNamaKriteria(), id)) {
            throw new IllegalArgumentException("Nama kriteria sudah terdaftar");
        }

        KriteriaPenilaian updateData = existingKriteria.get();
        updateData.setKodeKriteria(kriteria.getKodeKriteria());
        updateData.setNamaKriteria(kriteria.getNamaKriteria());
        updateData.setBobot(kriteria.getBobot());

        return kriteriaRepository.save(updateData);
    }

    @Override
    public void deleteKriteria(Long id) {
        Optional<KriteriaPenilaian> kriteria = kriteriaRepository.findById(id);

        if (!kriteria.isPresent()) {
            throw new IllegalArgumentException("Kriteria dengan ID " + id + " tidak ditemukan");
        }

        // Soft delete - set status to inactive
        KriteriaPenilaian data = kriteria.get();
        data.setStatusAktif(false);
        kriteriaRepository.save(data);
    }

    @Override
    public void permanentDeleteKriteria(Long id) {
        Optional<KriteriaPenilaian> kriteria = kriteriaRepository.findById(id);

        if (!kriteria.isPresent()) {
            throw new IllegalArgumentException("Kriteria dengan ID " + id + " tidak ditemukan");
        }

        kriteriaRepository.deleteById(id);
    }

    @Override
    public void activateKriteria(Long id) {
        Optional<KriteriaPenilaian> kriteria = kriteriaRepository.findById(id);

        if (!kriteria.isPresent()) {
            throw new IllegalArgumentException("Kriteria dengan ID " + id + " tidak ditemukan");
        }

        KriteriaPenilaian data = kriteria.get();
        data.setStatusAktif(true);
        kriteriaRepository.save(data);
    }

    @Override
    public void deactivateKriteria(Long id) {
        Optional<KriteriaPenilaian> kriteria = kriteriaRepository.findById(id);

        if (!kriteria.isPresent()) {
            throw new IllegalArgumentException("Kriteria dengan ID " + id + " tidak ditemukan");
        }

        KriteriaPenilaian data = kriteria.get();
        data.setStatusAktif(false);
        kriteriaRepository.save(data);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isKodeKriteriaExists(String kodeKriteria) {
        return kriteriaRepository.countByKodeKriteria(kodeKriteria) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isNamaKriteriaExists(String namaKriteria) {
        return kriteriaRepository.countByNamaKriteria(namaKriteria) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isKodeKriteriaExistsExcludingId(String kodeKriteria, Long id) {
        return kriteriaRepository.countByKodeKriteriaExcludingId(kodeKriteria, id) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isNamaKriteriaExistsExcludingId(String namaKriteria, Long id) {
        return kriteriaRepository.countByNamaKriteriaExcludingId(namaKriteria, id) > 0;
    }
}

