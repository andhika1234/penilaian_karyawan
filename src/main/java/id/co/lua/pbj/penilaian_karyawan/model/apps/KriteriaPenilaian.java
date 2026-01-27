package id.co.lua.pbj.penilaian_karyawan.model.apps;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "kriteria_penilaian")
public class KriteriaPenilaian extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Kode kriteria wajib diisi")
    @Size(min = 5, max = 10, message = "Kode kriteria harus 5-10 karakter")
    @Pattern(regexp = "K-[0-9]{3}", message = "Format kode kriteria tidak valid (contoh: K-001)")
    @Column(name = "kode_kriteria", nullable = false, unique = true, length = 10)
    private String kodeKriteria;

    @NotBlank(message = "Nama kriteria wajib diisi")
    @Column(name = "nama_kriteria", nullable = false)
    private String namaKriteria;

    @Column(name = "status_aktif")
    private Boolean statusAktif = true;
}

