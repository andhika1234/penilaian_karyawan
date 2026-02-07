package id.co.lua.pbj.penilaian_karyawan.model.apps;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "penilaian_karyawan")
public class PenilaianKaryawan extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Karyawan wajib dipilih")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "karyawan_id", nullable = false)
    private Karyawan karyawan;

    @NotNull(message = "Divisi wajib dipilih")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "divisi_id", nullable = false)
    private Divisi divisi;

    @NotNull(message = "Jabatan wajib dipilih")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jabatan_id", nullable = false)
    private Jabatan jabatan;

    @NotNull(message = "Bulan penilaian wajib diisi")
    @Column(name = "bulan", nullable = false)
    private Integer bulan;

    @NotNull(message = "Tahun penilaian wajib diisi")
    @Column(name = "tahun", nullable = false)
    private Integer tahun;

    @Column(name = "tanggal_penilaian")
    private LocalDate tanggalPenilaian;

    // Detail Penilaian per Kriteria (OneToMany relationship)
    @OneToMany(mappedBy = "penilaianKaryawan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DetailPenilaian> detailPenilaianList = new ArrayList<>();

    @Column(name = "total_nilai")
    private Double totalNilai;

    @Column(name = "nilai_rata_rata")
    private Double nilaiRataRata;

    @Column(name = "kategori_penilaian", length = 50)
    private String kategoriPenilaian;

    @Column(name = "catatan", columnDefinition = "TEXT")
    private String catatan;

    @Column(name = "status_aktif")
    private Boolean statusAktif = true;

    /**
     * Helper method to add detail penilaian
     */
    public void addDetailPenilaian(DetailPenilaian detail) {
        detailPenilaianList.add(detail);
        detail.setPenilaianKaryawan(this);
    }

    /**
     * Helper method to remove detail penilaian
     */
    public void removeDetailPenilaian(DetailPenilaian detail) {
        detailPenilaianList.remove(detail);
        detail.setPenilaianKaryawan(null);
    }

    /**
     * Get nilai by kriteria name for template rendering
     */
    public Integer getNilaiByKriteria(String namaKriteria) {
        if (namaKriteria == null || detailPenilaianList == null) return 0;

        return detailPenilaianList.stream()
            .filter(detail -> detail.getKriteriaPenilaian() != null &&
                            namaKriteria.equals(detail.getKriteriaPenilaian().getNamaKriteria()))
            .findFirst()
            .map(DetailPenilaian::getNilai)
            .orElse(0);
    }

    /**
     * Get nilai by kriteria kode (e.g., K-001, K-002, etc.)
     */
    public Integer getNilaiByKode(String kodeKriteria) {
        if (kodeKriteria == null || detailPenilaianList == null) return 0;

        return detailPenilaianList.stream()
            .filter(detail -> detail.getKriteriaPenilaian() != null &&
                            kodeKriteria.equals(detail.getKriteriaPenilaian().getKodeKriteria()))
            .findFirst()
            .map(DetailPenilaian::getNilai)
            .orElse(0);
    }

    /**
     * Get nilai K1 (kriteria pertama berdasarkan urutan)
     */
    public Integer getK1() {
        return getNilaiByIndex(0);
    }

    /**
     * Get nilai K2 (kriteria kedua berdasarkan urutan)
     */
    public Integer getK2() {
        return getNilaiByIndex(1);
    }

    /**
     * Get nilai K3 (kriteria ketiga berdasarkan urutan)
     */
    public Integer getK3() {
        return getNilaiByIndex(2);
    }

    /**
     * Get nilai K4 (kriteria keempat berdasarkan urutan)
     */
    public Integer getK4() {
        return getNilaiByIndex(3);
    }

    /**
     * Get nilai K5 (kriteria kelima berdasarkan urutan)
     */
    public Integer getK5() {
        return getNilaiByIndex(4);
    }

    /**
     * Helper method to get nilai by index position
     */
    private Integer getNilaiByIndex(int index) {
        if (detailPenilaianList == null || detailPenilaianList.isEmpty()) return 0;
        if (index >= detailPenilaianList.size()) return 0;

        // Sort by kodeKriteria to maintain consistent order
        List<DetailPenilaian> sortedDetails = detailPenilaianList.stream()
            .filter(detail -> detail.getKriteriaPenilaian() != null)
            .sorted((d1, d2) -> {
                String kode1 = d1.getKriteriaPenilaian().getKodeKriteria();
                String kode2 = d2.getKriteriaPenilaian().getKodeKriteria();
                return kode1 != null && kode2 != null ? kode1.compareTo(kode2) : 0;
            })
            .toList();

        return sortedDetails.size() > index ? sortedDetails.get(index).getNilai() : 0;
    }

    /**
     * Calculate total nilai and rata-rata before persisting
     */
    @PrePersist
    @PreUpdate
    public void calculateNilai() {
        if (detailPenilaianList != null && !detailPenilaianList.isEmpty()) {
            int sum = detailPenilaianList.stream()
                .mapToInt(DetailPenilaian::getNilai)
                .sum();

            int count = detailPenilaianList.size();

            totalNilai = (double) sum;
            nilaiRataRata = count > 0 ? (double) sum / count : 0.0;

            // Set kategori based on rata-rata
            if (nilaiRataRata >= 4.5) {
                kategoriPenilaian = "Sangat Baik";
            } else if (nilaiRataRata >= 3.5) {
                kategoriPenilaian = "Baik";
            } else if (nilaiRataRata >= 2.5) {
                kategoriPenilaian = "Cukup";
            } else if (nilaiRataRata >= 1.5) {
                kategoriPenilaian = "Kurang";
            } else {
                kategoriPenilaian = "Sangat Kurang";
            }
        }

        if (tanggalPenilaian == null) {
            tanggalPenilaian = LocalDate.now();
        }
    }
}

