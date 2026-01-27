package id.co.lua.pbj.penilaian_karyawan.services.models;

import id.co.lua.pbj.penilaian_karyawan.model.apps.Karyawan;
import id.co.lua.pbj.penilaian_karyawan.model.apps.PenilaianKaryawan;
import id.co.lua.pbj.penilaian_karyawan.model.dto.RekapTahunanDTO;
import id.co.lua.pbj.penilaian_karyawan.model.repositories.apps.PenilaianKaryawanRepository;
import id.co.lua.pbj.penilaian_karyawan.model.repositories.apps.KaryawanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PenilaianKaryawanServiceImpl implements PenilaianKaryawanService {

    @Autowired
    private PenilaianKaryawanRepository penilaianKaryawanRepository;

    @Autowired
    private KaryawanRepository karyawanRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PenilaianKaryawan> getAllPenilaian() {
        return penilaianKaryawanRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PenilaianKaryawan> getAllActivePenilaian() {
        return penilaianKaryawanRepository.findAllActivePenilaian();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PenilaianKaryawan> getPenilaianById(Long id) {
        return penilaianKaryawanRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PenilaianKaryawan> getPenilaianByKaryawan(Karyawan karyawan) {
        return penilaianKaryawanRepository.findByKaryawan(karyawan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PenilaianKaryawan> getPenilaianByKaryawanId(Long karyawanId) {
        return penilaianKaryawanRepository.findByKaryawanIdOrderByTahunDesc(karyawanId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PenilaianKaryawan> getPenilaianByBulanAndTahun(Integer bulan, Integer tahun) {
        return penilaianKaryawanRepository.findByBulanAndTahun(bulan, tahun);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PenilaianKaryawan> getPenilaianByTahun(Integer tahun) {
        return penilaianKaryawanRepository.findByTahun(tahun);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PenilaianKaryawan> getPenilaianByTahunOrderByNilai(Integer tahun) {
        return penilaianKaryawanRepository.findByTahunOrderByNilaiDesc(tahun);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PenilaianKaryawan> searchPenilaian(String keyword) {
        return penilaianKaryawanRepository.searchPenilaian(keyword);
    }

    @Override
    public PenilaianKaryawan savePenilaian(PenilaianKaryawan penilaian) {
        // Validate karyawan exists
        if (penilaian.getKaryawan() == null || penilaian.getKaryawan().getId() == null) {
            throw new IllegalArgumentException("Karyawan wajib dipilih");
        }

        // Check if penilaian already exists for this karyawan in this period
        if (isPenilaianExists(penilaian.getKaryawan().getId(), penilaian.getBulan(), penilaian.getTahun())) {
            throw new IllegalArgumentException(
                "Penilaian untuk karyawan ini pada bulan dan tahun yang sama sudah ada"
            );
        }

        // Validate bulan range
        if (penilaian.getBulan() < 1 || penilaian.getBulan() > 12) {
            throw new IllegalArgumentException("Bulan harus antara 1-12");
        }

        // Set default status to active
        if (penilaian.getStatusAktif() == null) {
            penilaian.setStatusAktif(true);
        }

        return penilaianKaryawanRepository.save(penilaian);
    }

    @Override
    public PenilaianKaryawan updatePenilaian(Long id, PenilaianKaryawan penilaian) {
        Optional<PenilaianKaryawan> existingPenilaian = penilaianKaryawanRepository.findById(id);

        if (!existingPenilaian.isPresent()) {
            throw new IllegalArgumentException("Penilaian dengan ID " + id + " tidak ditemukan");
        }

        // Check if penilaian already exists for this karyawan in this period (excluding current record)
        if (isPenilaianExistsExcludingId(
                penilaian.getKaryawan().getId(),
                penilaian.getBulan(),
                penilaian.getTahun(),
                id)) {
            throw new IllegalArgumentException(
                "Penilaian untuk karyawan ini pada bulan dan tahun yang sama sudah ada"
            );
        }

        // Validate bulan range
        if (penilaian.getBulan() < 1 || penilaian.getBulan() > 12) {
            throw new IllegalArgumentException("Bulan harus antara 1-12");
        }

        PenilaianKaryawan updateData = existingPenilaian.get();
        updateData.setKaryawan(penilaian.getKaryawan());
        updateData.setDivisi(penilaian.getDivisi());
        updateData.setJabatan(penilaian.getJabatan());
        updateData.setBulan(penilaian.getBulan());
        updateData.setTahun(penilaian.getTahun());
        updateData.setCatatan(penilaian.getCatatan());

        // Clear existing detail and add new ones
        updateData.getDetailPenilaianList().clear();
        if (penilaian.getDetailPenilaianList() != null) {
            for (var detail : penilaian.getDetailPenilaianList()) {
                updateData.addDetailPenilaian(detail);
            }
        }

        return penilaianKaryawanRepository.save(updateData);
    }

    @Override
    public void deletePenilaian(Long id) {
        Optional<PenilaianKaryawan> penilaian = penilaianKaryawanRepository.findById(id);

        if (!penilaian.isPresent()) {
            throw new IllegalArgumentException("Penilaian dengan ID " + id + " tidak ditemukan");
        }

        // Soft delete - set status to inactive
        PenilaianKaryawan data = penilaian.get();
        data.setStatusAktif(false);
        penilaianKaryawanRepository.save(data);
    }

    @Override
    public void permanentDeletePenilaian(Long id) {
        Optional<PenilaianKaryawan> penilaian = penilaianKaryawanRepository.findById(id);

        if (!penilaian.isPresent()) {
            throw new IllegalArgumentException("Penilaian dengan ID " + id + " tidak ditemukan");
        }

        penilaianKaryawanRepository.deleteById(id);
    }

    @Override
    public void activatePenilaian(Long id) {
        Optional<PenilaianKaryawan> penilaian = penilaianKaryawanRepository.findById(id);

        if (!penilaian.isPresent()) {
            throw new IllegalArgumentException("Penilaian dengan ID " + id + " tidak ditemukan");
        }

        PenilaianKaryawan data = penilaian.get();
        data.setStatusAktif(true);
        penilaianKaryawanRepository.save(data);
    }

    @Override
    public void deactivatePenilaian(Long id) {
        Optional<PenilaianKaryawan> penilaian = penilaianKaryawanRepository.findById(id);

        if (!penilaian.isPresent()) {
            throw new IllegalArgumentException("Penilaian dengan ID " + id + " tidak ditemukan");
        }

        PenilaianKaryawan data = penilaian.get();
        data.setStatusAktif(false);
        penilaianKaryawanRepository.save(data);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPenilaianExists(Long karyawanId, Integer bulan, Integer tahun) {
        return penilaianKaryawanRepository.countByKaryawanAndBulanAndTahun(karyawanId, bulan, tahun) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPenilaianExistsExcludingId(Long karyawanId, Integer bulan, Integer tahun, Long id) {
        return penilaianKaryawanRepository.countByKaryawanAndBulanAndTahunExcludingId(
            karyawanId, bulan, tahun, id
        ) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageNilaiByKaryawan(Long karyawanId) {
        Double average = penilaianKaryawanRepository.getAverageNilaiByKaryawan(karyawanId);
        return average != null ? average : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PenilaianKaryawan> getPeringkatByBulanAndTahun(Integer bulan, Integer tahun) {
        return penilaianKaryawanRepository.findByBulanAndTahunOrderByTotalNilaiDesc(bulan, tahun);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PenilaianKaryawan> getPeringkatByTahun(Integer tahun) {
        return penilaianKaryawanRepository.findByTahunOrderByTotalNilaiDesc(tahun);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RekapTahunanDTO> getRekapTahunanByTahun(Integer tahun) {
        List<Object[]> results = penilaianKaryawanRepository.getRekapTahunanByTahun(tahun);
        return convertToRekapTahunanDTO(results);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RekapTahunanDTO> getAllRekapTahunan() {
        List<Object[]> results = penilaianKaryawanRepository.getAllRekapTahunan();
        return convertToRekapTahunanDTO(results);
    }

    /**
     * Convert Object[] to RekapTahunanDTO
     */
    private List<RekapTahunanDTO> convertToRekapTahunanDTO(List<Object[]> results) {
        List<RekapTahunanDTO> dtoList = new ArrayList<>();
        for (Object[] row : results) {
            RekapTahunanDTO dto = new RekapTahunanDTO();
            dto.setKaryawanId(((Number) row[0]).longValue());
            dto.setNamaKaryawan((String) row[1]);
            dto.setNamaDivisi((String) row[2]);
            dto.setNamaJabatan((String) row[3]);
            dto.setTahun((Integer) row[4]);
            dto.setTotalBobotTahunan(((Number) row[5]).doubleValue());
            dto.setRataRataTahunan(((Number) row[6]).doubleValue());
            dto.setJumlahBulan(((Number) row[7]).longValue());
            dtoList.add(dto);
        }
        return dtoList;
    }
}

