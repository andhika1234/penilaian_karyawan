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
@Table(name = "divisi")
public class Divisi extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Kode divisi wajib diisi")
    @Size(min = 6, max = 10, message = "Kode divisi harus 6-10 karakter")
    @Pattern(regexp = "[A-Z]{2,5}-[0-9]{3}", message = "Format kode divisi tidak valid (contoh: IT-001)")
    @Column(name = "kode_divisi", nullable = false, unique = true, length = 10)
    private String kodeDivisi;

    @NotBlank(message = "Nama divisi wajib diisi")
    @Column(name = "nama_divisi", nullable = false)
    private String namaDivisi;

    @Column(name = "status_aktif")
    private Boolean statusAktif = true;
}

