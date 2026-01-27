package id.co.lua.pbj.penilaian_karyawan.services.models;

import id.co.lua.pbj.penilaian_karyawan.model.apps.Divisi;
import id.co.lua.pbj.penilaian_karyawan.model.repositories.apps.DivisiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DivisiServiceImpl implements DivisiService {

    @Autowired
    private DivisiRepository divisiRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Divisi> getAllDivisi() {
        return divisiRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Divisi> getAllDivisiOrderByNama() {
        return divisiRepository.findAllOrderByNama();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Divisi> getAllActiveDivisi() {
        return divisiRepository.findAllActiveDivisi();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Divisi> getDivisiById(Long id) {
        return divisiRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Divisi> getDivisiByKodeDivisi(String kodeDivisi) {
        return divisiRepository.findByKodeDivisi(kodeDivisi);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Divisi> getDivisiByNamaDivisi(String namaDivisi) {
        return divisiRepository.findByNamaDivisi(namaDivisi);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Divisi> searchDivisi(String keyword) {
        return divisiRepository.searchDivisi(keyword);
    }

    @Override
    public Divisi saveDivisi(Divisi divisi) {
        // Validate kode divisi uniqueness
        if (isKodeDivisiExists(divisi.getKodeDivisi())) {
            throw new IllegalArgumentException("Kode divisi sudah terdaftar");
        }

        // Validate nama divisi uniqueness
        if (isNamaDivisiExists(divisi.getNamaDivisi())) {
            throw new IllegalArgumentException("Nama divisi sudah terdaftar");
        }

        // Set default status to active
        if (divisi.getStatusAktif() == null) {
            divisi.setStatusAktif(true);
        }

        return divisiRepository.save(divisi);
    }

    @Override
    public Divisi updateDivisi(Long id, Divisi divisi) {
        Optional<Divisi> existingDivisi = divisiRepository.findById(id);

        if (!existingDivisi.isPresent()) {
            throw new IllegalArgumentException("Divisi dengan ID " + id + " tidak ditemukan");
        }

        // Validate kode divisi uniqueness (excluding current record)
        if (isKodeDivisiExistsExcludingId(divisi.getKodeDivisi(), id)) {
            throw new IllegalArgumentException("Kode divisi sudah terdaftar");
        }

        // Validate nama divisi uniqueness (excluding current record)
        if (isNamaDivisiExistsExcludingId(divisi.getNamaDivisi(), id)) {
            throw new IllegalArgumentException("Nama divisi sudah terdaftar");
        }

        Divisi updateData = existingDivisi.get();
        updateData.setKodeDivisi(divisi.getKodeDivisi());
        updateData.setNamaDivisi(divisi.getNamaDivisi());

        return divisiRepository.save(updateData);
    }

    @Override
    public void deleteDivisi(Long id) {
        Optional<Divisi> divisi = divisiRepository.findById(id);

        if (!divisi.isPresent()) {
            throw new IllegalArgumentException("Divisi dengan ID " + id + " tidak ditemukan");
        }

        // Soft delete - set status to inactive
        Divisi data = divisi.get();
        data.setStatusAktif(false);
        divisiRepository.save(data);
    }

    @Override
    public void permanentDeleteDivisi(Long id) {
        Optional<Divisi> divisi = divisiRepository.findById(id);

        if (!divisi.isPresent()) {
            throw new IllegalArgumentException("Divisi dengan ID " + id + " tidak ditemukan");
        }

        divisiRepository.deleteById(id);
    }

    @Override
    public void activateDivisi(Long id) {
        Optional<Divisi> divisi = divisiRepository.findById(id);

        if (!divisi.isPresent()) {
            throw new IllegalArgumentException("Divisi dengan ID " + id + " tidak ditemukan");
        }

        Divisi data = divisi.get();
        data.setStatusAktif(true);
        divisiRepository.save(data);
    }

    @Override
    public void deactivateDivisi(Long id) {
        Optional<Divisi> divisi = divisiRepository.findById(id);

        if (!divisi.isPresent()) {
            throw new IllegalArgumentException("Divisi dengan ID " + id + " tidak ditemukan");
        }

        Divisi data = divisi.get();
        data.setStatusAktif(false);
        divisiRepository.save(data);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isKodeDivisiExists(String kodeDivisi) {
        return divisiRepository.countByKodeDivisi(kodeDivisi) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isNamaDivisiExists(String namaDivisi) {
        return divisiRepository.countByNamaDivisi(namaDivisi) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isKodeDivisiExistsExcludingId(String kodeDivisi, Long id) {
        return divisiRepository.countByKodeDivisiExcludingId(kodeDivisi, id) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isNamaDivisiExistsExcludingId(String namaDivisi, Long id) {
        return divisiRepository.countByNamaDivisiExcludingId(namaDivisi, id) > 0;
    }
}

