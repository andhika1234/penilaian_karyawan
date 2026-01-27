package id.co.lua.pbj.penilaian_karyawan.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RekapTahunanDTO {
    private Long karyawanId;
    private String namaKaryawan;
    private String namaDivisi;
    private String namaJabatan;
    private Integer tahun;
    private Double totalBobotTahunan;
    private Double rataRataTahunan;
    private Long jumlahBulan;
}

