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

