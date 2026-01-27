package id.co.lua.pbj.penilaian_karyawan.services.models;

import id.co.lua.pbj.penilaian_karyawan.model.apps.Jabatan;
import id.co.lua.pbj.penilaian_karyawan.model.apps.Karyawan;
import id.co.lua.pbj.penilaian_karyawan.model.repositories.apps.KaryawanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class KaryawanServiceImpl implements KaryawanService {

    @Autowired
    private KaryawanRepository karyawanRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Karyawan> getAllKaryawan() {
        return karyawanRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Karyawan> getAllActiveKaryawan() {
        return karyawanRepository.findAllActiveKaryawan();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Karyawan> getKaryawanById(Long id) {
        return karyawanRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Karyawan> getKaryawanByNik(String nik) {
        return karyawanRepository.findByNik(nik);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Karyawan> getKaryawanByEmail(String email) {
        return karyawanRepository.findByEmailKaryawan(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Karyawan> getKaryawanByJabatan(Jabatan jabatan) {
        return karyawanRepository.findByJabatan(jabatan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Karyawan> searchKaryawan(String keyword) {
        return karyawanRepository.searchKaryawan(keyword);
    }

    @Override
    public Karyawan saveKaryawan(Karyawan karyawan) {
        // Validate NIK uniqueness
        if (isNikExists(karyawan.getNik())) {
            throw new IllegalArgumentException("NIK sudah terdaftar");
        }

        // Validate email uniqueness
        if (isEmailExists(karyawan.getEmailKaryawan())) {
            throw new IllegalArgumentException("Email sudah terdaftar");
        }

        // Set default status to active
        if (karyawan.getStatusAktif() == null) {
            karyawan.setStatusAktif(true);
        }

        return karyawanRepository.save(karyawan);
    }

    @Override
    public Karyawan updateKaryawan(Long id, Karyawan karyawan) {
        Optional<Karyawan> existingKaryawan = karyawanRepository.findById(id);

        if (!existingKaryawan.isPresent()) {
            throw new IllegalArgumentException("Karyawan dengan ID " + id + " tidak ditemukan");
        }

        // Validate NIK uniqueness (excluding current record)
        if (isNikExistsExcludingId(karyawan.getNik(), id)) {
            throw new IllegalArgumentException("NIK sudah terdaftar");
        }

        // Validate email uniqueness (excluding current record)
        if (isEmailExistsExcludingId(karyawan.getEmailKaryawan(), id)) {
            throw new IllegalArgumentException("Email sudah terdaftar");
        }

        Karyawan updateData = existingKaryawan.get();
        updateData.setNamaKaryawan(karyawan.getNamaKaryawan());
        updateData.setNik(karyawan.getNik());
        updateData.setNomorTelepon(karyawan.getNomorTelepon());
        updateData.setEmailKaryawan(karyawan.getEmailKaryawan());
        updateData.setDivisi(karyawan.getDivisi());
        updateData.setJabatan(karyawan.getJabatan());

        return karyawanRepository.save(updateData);
    }

    @Override
    public void deleteKaryawan(Long id) {
        Optional<Karyawan> karyawan = karyawanRepository.findById(id);

        if (!karyawan.isPresent()) {
            throw new IllegalArgumentException("Karyawan dengan ID " + id + " tidak ditemukan");
        }

        // Soft delete - set status to inactive
        Karyawan data = karyawan.get();
        data.setStatusAktif(false);
        karyawanRepository.save(data);
    }

    @Override
    public void permanentDeleteKaryawan(Long id) {
        Optional<Karyawan> karyawan = karyawanRepository.findById(id);

        if (!karyawan.isPresent()) {
            throw new IllegalArgumentException("Karyawan dengan ID " + id + " tidak ditemukan");
        }

        karyawanRepository.deleteById(id);
    }

    @Override
    public void activateKaryawan(Long id) {
        Optional<Karyawan> karyawan = karyawanRepository.findById(id);

        if (!karyawan.isPresent()) {
            throw new IllegalArgumentException("Karyawan dengan ID " + id + " tidak ditemukan");
        }

        Karyawan data = karyawan.get();
        data.setStatusAktif(true);
        karyawanRepository.save(data);
    }

    @Override
    public void deactivateKaryawan(Long id) {
        Optional<Karyawan> karyawan = karyawanRepository.findById(id);

        if (!karyawan.isPresent()) {
            throw new IllegalArgumentException("Karyawan dengan ID " + id + " tidak ditemukan");
        }

        Karyawan data = karyawan.get();
        data.setStatusAktif(false);
        karyawanRepository.save(data);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isNikExists(String nik) {
        return karyawanRepository.countByNik(nik) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailExists(String email) {
        return karyawanRepository.countByEmail(email) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isNikExistsExcludingId(String nik, Long id) {
        return karyawanRepository.countByNikExcludingId(nik, id) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailExistsExcludingId(String email, Long id) {
        return karyawanRepository.countByEmailExcludingId(email, id) > 0;
    }
}

