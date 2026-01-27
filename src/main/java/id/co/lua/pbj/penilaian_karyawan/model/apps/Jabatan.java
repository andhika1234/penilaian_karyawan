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
@Table(name = "jabatan")
public class Jabatan extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Kode jabatan wajib diisi")
    @Size(min = 5, max = 10, message = "Kode jabatan harus 5-10 karakter")
    @Pattern(regexp = "J-[0-9]{3}", message = "Format kode jabatan tidak valid (contoh: J-001)")
    @Column(name = "kode_jabatan", nullable = false, unique = true, length = 10)
    private String kodeJabatan;

    @NotBlank(message = "Nama jabatan wajib diisi")
    @Column(name = "nama_jabatan", nullable = false)
    private String namaJabatan;

    @Column(name = "status_aktif")
    private Boolean statusAktif = true;
}

