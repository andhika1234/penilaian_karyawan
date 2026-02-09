package id.co.lua.pbj.penilaian_karyawan.model.apps;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "normalisasi")
public class Normalisasi extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Penilaian karyawan wajib dipilih")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "penilaian_karyawan_id", nullable = false)
    private PenilaianKaryawan penilaianKaryawan;

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

    // Nilai normalisasi (dibagi 5)
    @Column(name = "k1_normalisasi")
    private Double k1Normalisasi;

    @Column(name = "k2_normalisasi")
    private Double k2Normalisasi;

    @Column(name = "k3_normalisasi")
    private Double k3Normalisasi;

    @Column(name = "k4_normalisasi")
    private Double k4Normalisasi;

    @Column(name = "k5_normalisasi")
    private Double k5Normalisasi;

    @Column(name = "total_normalisasi")
    private Double totalNormalisasi;

    @Column(name = "catatan", columnDefinition = "TEXT")
    private String catatan;

    @Column(name = "status_aktif")
    private Boolean statusAktif = true;

    /**
     * Calculate normalization value (nilai / 5)
     */
    public static Double normalizeValue(Integer nilai) {
        if (nilai == null) return 0.0;
        return nilai / 5.0;
    }

    /**
     * Calculate total normalization
     */
    public void calculateTotalNormalisasi() {
        double total = 0.0;
        if (k1Normalisasi != null) total += k1Normalisasi;
        if (k2Normalisasi != null) total += k2Normalisasi;
        if (k3Normalisasi != null) total += k3Normalisasi;
        if (k4Normalisasi != null) total += k4Normalisasi;
        if (k5Normalisasi != null) total += k5Normalisasi;
        this.totalNormalisasi = total;
    }
}

