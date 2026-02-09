package id.co.lua.pbj.penilaian_karyawan.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NilaiReferensiDTO {
    private Long normalisasiId;
    private Long karyawanId;
    private String namaKaryawan;
    private String namaDivisi;
    private String namaJabatan;
    private Integer bulan;
    private Integer tahun;

    // Nilai normalisasi
    private Double k1Normalisasi;
    private Double k2Normalisasi;
    private Double k3Normalisasi;
    private Double k4Normalisasi;
    private Double k5Normalisasi;

    // Bobot kriteria
    private Integer bobotK1;
    private Integer bobotK2;
    private Integer bobotK3;
    private Integer bobotK4;
    private Integer bobotK5;

    // Hasil perhitungan (normalisasi * bobot)
    private Double hasilK1;
    private Double hasilK2;
    private Double hasilK3;
    private Double hasilK4;
    private Double hasilK5;

    // Total nilai referensi
    private Double nilaiReferensi;

    /**
     * Calculate nilai referensi from normalisasi and bobot
     */
    public void calculateNilaiReferensi() {
        hasilK1 = (k1Normalisasi != null && bobotK1 != null) ? k1Normalisasi * bobotK1 : 0.0;
        hasilK2 = (k2Normalisasi != null && bobotK2 != null) ? k2Normalisasi * bobotK2 : 0.0;
        hasilK3 = (k3Normalisasi != null && bobotK3 != null) ? k3Normalisasi * bobotK3 : 0.0;
        hasilK4 = (k4Normalisasi != null && bobotK4 != null) ? k4Normalisasi * bobotK4 : 0.0;
        hasilK5 = (k5Normalisasi != null && bobotK5 != null) ? k5Normalisasi * bobotK5 : 0.0;

        nilaiReferensi = hasilK1 + hasilK2 + hasilK3 + hasilK4 + hasilK5;
    }
}

