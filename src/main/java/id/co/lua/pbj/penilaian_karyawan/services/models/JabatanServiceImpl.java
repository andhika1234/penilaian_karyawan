package id.co.lua.pbj.penilaian_karyawan.services.models;

import id.co.lua.pbj.penilaian_karyawan.model.apps.Jabatan;
import id.co.lua.pbj.penilaian_karyawan.model.repositories.apps.JabatanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class JabatanServiceImpl implements JabatanService {

    @Autowired
    private JabatanRepository jabatanRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Jabatan> getAllJabatan() {
        return jabatanRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Jabatan> getAllJabatanOrderByNama() {
        return jabatanRepository.findAllOrderByNama();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Jabatan> getAllActiveJabatan() {
        return jabatanRepository.findAllActiveJabatan();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Jabatan> getJabatanById(Long id) {
        return jabatanRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Jabatan> getJabatanByKodeJabatan(String kodeJabatan) {
        return jabatanRepository.findByKodeJabatan(kodeJabatan);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Jabatan> getJabatanByNamaJabatan(String namaJabatan) {
        return jabatanRepository.findByNamaJabatan(namaJabatan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Jabatan> searchJabatan(String keyword) {
        return jabatanRepository.searchJabatan(keyword);
    }

    @Override
    public Jabatan saveJabatan(Jabatan jabatan) {
        // Validate kode jabatan uniqueness
        if (isKodeJabatanExists(jabatan.getKodeJabatan())) {
            throw new IllegalArgumentException("Kode jabatan sudah terdaftar");
        }

        // Validate nama jabatan uniqueness
        if (isNamaJabatanExists(jabatan.getNamaJabatan())) {
            throw new IllegalArgumentException("Nama jabatan sudah terdaftar");
        }

        // Set default status to active
        if (jabatan.getStatusAktif() == null) {
            jabatan.setStatusAktif(true);
        }

        return jabatanRepository.save(jabatan);
    }

    @Override
    public Jabatan updateJabatan(Long id, Jabatan jabatan) {
        Optional<Jabatan> existingJabatan = jabatanRepository.findById(id);

        if (!existingJabatan.isPresent()) {
            throw new IllegalArgumentException("Jabatan dengan ID " + id + " tidak ditemukan");
        }

        // Validate kode jabatan uniqueness (excluding current record)
        if (isKodeJabatanExistsExcludingId(jabatan.getKodeJabatan(), id)) {
            throw new IllegalArgumentException("Kode jabatan sudah terdaftar");
        }

        // Validate nama jabatan uniqueness (excluding current record)
        if (isNamaJabatanExistsExcludingId(jabatan.getNamaJabatan(), id)) {
            throw new IllegalArgumentException("Nama jabatan sudah terdaftar");
        }

        Jabatan updateData = existingJabatan.get();
        updateData.setKodeJabatan(jabatan.getKodeJabatan());
        updateData.setNamaJabatan(jabatan.getNamaJabatan());

        return jabatanRepository.save(updateData);
    }

    @Override
    public void deleteJabatan(Long id) {
        Optional<Jabatan> jabatan = jabatanRepository.findById(id);

        if (!jabatan.isPresent()) {
            throw new IllegalArgumentException("Jabatan dengan ID " + id + " tidak ditemukan");
        }

        // Soft delete - set status to inactive
        Jabatan data = jabatan.get();
        data.setStatusAktif(false);
        jabatanRepository.save(data);
    }

    @Override
    public void permanentDeleteJabatan(Long id) {
        Optional<Jabatan> jabatan = jabatanRepository.findById(id);

        if (!jabatan.isPresent()) {
            throw new IllegalArgumentException("Jabatan dengan ID " + id + " tidak ditemukan");
        }

        jabatanRepository.deleteById(id);
    }

    @Override
    public void activateJabatan(Long id) {
        Optional<Jabatan> jabatan = jabatanRepository.findById(id);

        if (!jabatan.isPresent()) {
            throw new IllegalArgumentException("Jabatan dengan ID " + id + " tidak ditemukan");
        }

        Jabatan data = jabatan.get();
        data.setStatusAktif(true);
        jabatanRepository.save(data);
    }

    @Override
    public void deactivateJabatan(Long id) {
        Optional<Jabatan> jabatan = jabatanRepository.findById(id);

        if (!jabatan.isPresent()) {
            throw new IllegalArgumentException("Jabatan dengan ID " + id + " tidak ditemukan");
        }

        Jabatan data = jabatan.get();
        data.setStatusAktif(false);
        jabatanRepository.save(data);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isKodeJabatanExists(String kodeJabatan) {
        return jabatanRepository.countByKodeJabatan(kodeJabatan) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isNamaJabatanExists(String namaJabatan) {
        return jabatanRepository.countByNamaJabatan(namaJabatan) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isKodeJabatanExistsExcludingId(String kodeJabatan, Long id) {
        return jabatanRepository.countByKodeJabatanExcludingId(kodeJabatan, id) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isNamaJabatanExistsExcludingId(String namaJabatan, Long id) {
        return jabatanRepository.countByNamaJabatanExcludingId(namaJabatan, id) > 0;
    }
}

