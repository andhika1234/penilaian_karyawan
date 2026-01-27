package id.co.lua.pbj.penilaian_karyawan.model.apps;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "detail_penilaian")
public class DetailPenilaian extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Penilaian karyawan wajib diisi")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "penilaian_karyawan_id", nullable = false)
    private PenilaianKaryawan penilaianKaryawan;

    @NotNull(message = "Kriteria penilaian wajib dipilih")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "kriteria_penilaian_id", nullable = false)
    private KriteriaPenilaian kriteriaPenilaian;

    @NotNull(message = "Nilai wajib diisi")
    @Min(value = 1, message = "Nilai minimal adalah 1")
    @Max(value = 5, message = "Nilai maksimal adalah 5")
    @Column(name = "nilai", nullable = false)
    private Integer nilai;

    @Column(name = "catatan", columnDefinition = "TEXT")
    private String catatan;

    @Column(name = "status_aktif")
    private Boolean statusAktif = true;
}

