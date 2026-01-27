package id.co.lua.pbj.penilaian_karyawan.model.apps;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
@Table(name = "karyawan")
public class Karyawan extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nama karyawan wajib diisi")
    @Column(name = "nama_karyawan", nullable = false)
    private String namaKaryawan;

    @NotBlank(message = "NIK wajib diisi")
    @Size(min = 16, max = 16, message = "NIK harus 16 digit")
    @Pattern(regexp = "[0-9]{16}", message = "NIK harus berupa 16 digit angka")
    @Column(name = "nik", nullable = false, unique = true, length = 16)
    private String nik;

    @NotBlank(message = "Nomor telepon wajib diisi")
    @Pattern(regexp = "[0-9]{10,13}", message = "Nomor telepon harus 10-13 digit angka")
    @Column(name = "nomor_telepon", nullable = false, length = 13)
    private String nomorTelepon;

    @NotBlank(message = "Email karyawan wajib diisi")
    @Email(message = "Format email tidak valid")
    @Column(name = "email_karyawan", nullable = false, unique = true)
    private String emailKaryawan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "divisi_id", nullable = false)
    private Divisi divisi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jabatan_id", nullable = false)
    private Jabatan jabatan;

    @Column(name = "status_aktif")
    private Boolean statusAktif = true;
}

