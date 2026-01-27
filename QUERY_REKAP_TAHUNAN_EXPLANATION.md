# Penjelasan Query Rekap Tahunan

## Deskripsi
Query ini digunakan untuk menampilkan rekap tahunan karyawan dengan menghitung **total bobot penilaian selama 12 bulan** (atau berapa bulan yang tersedia dalam setahun). Karyawan dengan total bobot tertinggi akan berada di peringkat 1.

## Implementasi

### 1. Repository Layer (PenilaianKaryawanRepository.java)

#### Query untuk Rekap Tahunan Berdasarkan Tahun:
```java
@Query("SELECT p.karyawan.id as karyawanId, " +
       "p.karyawan.namaKaryawan as namaKaryawan, " +
       "p.divisi.namaDivisi as namaDivisi, " +
       "p.jabatan.namaJabatan as namaJabatan, " +
       "p.tahun as tahun, " +
       "SUM(p.totalNilai) as totalBobotTahunan, " +
       "AVG(p.nilaiRataRata) as rataRataTahunan, " +
       "COUNT(p.id) as jumlahBulan " +
       "FROM PenilaianKaryawan p " +
       "WHERE p.tahun = :tahun AND p.statusAktif = true " +
       "GROUP BY p.karyawan.id, p.karyawan.namaKaryawan, p.divisi.namaDivisi, p.jabatan.namaJabatan, p.tahun " +
       "ORDER BY SUM(p.totalNilai) DESC, AVG(p.nilaiRataRata) DESC, p.karyawan.namaKaryawan ASC")
List<Object[]> getRekapTahunanByTahun(@Param("tahun") Integer tahun);
```

**Penjelasan:**
- `SELECT` - Memilih field yang dibutuhkan dari relasi entity
- `SUM(p.totalNilai)` - Menghitung **total bobot** dari semua bulan dalam setahun
- `AVG(p.nilaiRataRata)` - Menghitung rata-rata dari nilai rata-rata bulanan
- `COUNT(p.id)` - Menghitung berapa bulan karyawan dinilai (idealnya 12 bulan)
- `WHERE p.tahun = :tahun` - Filter berdasarkan tahun yang dipilih
- `AND p.statusAktif = true` - Hanya data aktif
- `GROUP BY` - Mengelompokkan per karyawan (karena satu karyawan punya banyak penilaian per bulan)
- `ORDER BY SUM(p.totalNilai) DESC` - Urutkan berdasarkan total bobot tertinggi ke terendah

#### Query untuk Semua Rekap Tahunan:
```java
@Query("SELECT p.karyawan.id as karyawanId, " +
       "p.karyawan.namaKaryawan as namaKaryawan, " +
       "p.divisi.namaDivisi as namaDivisi, " +
       "p.jabatan.namaJabatan as namaJabatan, " +
       "p.tahun as tahun, " +
       "SUM(p.totalNilai) as totalBobotTahunan, " +
       "AVG(p.nilaiRataRata) as rataRataTahunan, " +
       "COUNT(p.id) as jumlahBulan " +
       "FROM PenilaianKaryawan p " +
       "WHERE p.statusAktif = true " +
       "GROUP BY p.karyawan.id, p.karyawan.namaKaryawan, p.divisi.namaDivisi, p.jabatan.namaJabatan, p.tahun " +
       "ORDER BY p.tahun DESC, SUM(p.totalNilai) DESC, AVG(p.nilaiRataRata) DESC, p.karyawan.namaKaryawan ASC")
List<Object[]> getAllRekapTahunan();
```

### 2. DTO Layer (RekapTahunanDTO.java)

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RekapTahunanDTO {
    private Long karyawanId;
    private String namaKaryawan;
    private String namaDivisi;
    private String namaJabatan;
    private Integer tahun;
    private Double totalBobotTahunan;    // SUM dari semua totalNilai per bulan
    private Double rataRataTahunan;      // AVG dari semua nilaiRataRata per bulan
    private Long jumlahBulan;            // Berapa bulan karyawan dinilai
}
```

### 3. Service Layer

#### Interface (PenilaianKaryawanService.java):
```java
List<RekapTahunanDTO> getRekapTahunanByTahun(Integer tahun);
List<RekapTahunanDTO> getAllRekapTahunan();
```

#### Implementation (PenilaianKaryawanServiceImpl.java):
```java
@Override
@Transactional(readOnly = true)
public List<RekapTahunanDTO> getRekapTahunanByTahun(Integer tahun) {
    List<Object[]> results = penilaianKaryawanRepository.getRekapTahunanByTahun(tahun);
    return convertToRekapTahunanDTO(results);
}

private List<RekapTahunanDTO> convertToRekapTahunanDTO(List<Object[]> results) {
    List<RekapTahunanDTO> dtoList = new ArrayList<>();
    for (Object[] row : results) {
        RekapTahunanDTO dto = new RekapTahunanDTO();
        dto.setKaryawanId(((Number) row[0]).longValue());
        dto.setNamaKaryawan((String) row[1]);
        dto.setNamaDivisi((String) row[2]);
        dto.setNamaJabatan((String) row[3]);
        dto.setTahun((Integer) row[4]);
        dto.setTotalBobotTahunan(((Number) row[5]).doubleValue());
        dto.setRataRataTahunan(((Number) row[6]).doubleValue());
        dto.setJumlahBulan(((Number) row[7]).longValue());
        dtoList.add(dto);
    }
    return dtoList;
}
```

### 4. Controller Layer (RekapTahunanController.java)

```java
@GetMapping("")
public ModelAndView index(ModelAndView mView,
                          @RequestParam(value = "tahun", required = false) Integer tahun,
                          @ModelAttribute(name = "resultCode") String resultCode,
                          @ModelAttribute(name = "resultMessage") String resultMessage) {
    
    // Default ke tahun sekarang jika tidak ada parameter
    if (tahun == null) {
        tahun = LocalDate.now().getYear();
    }

    // Ambil data rekap tahunan
    List<RekapTahunanDTO> rekapList = penilaianKaryawanService.getRekapTahunanByTahun(tahun);
    
    mView.addObject("rekapList", rekapList);
    mView.addObject("selectedTahun", tahun);
    mView.setViewName("pages/rekaptahunan/rekaptahunan-index");
    return mView;
}
```

### 5. View Layer (rekaptahunan-index.html)

#### Filter Form:
```html
<form method="get" th:action="@{/rekaptahunan}" id="filterForm">
    <select class="form-control" id="filterTahun" name="tahun">
        <option th:value="2026" th:selected="${selectedTahun == 2026}">2026</option>
        <option th:value="2025" th:selected="${selectedTahun == 2025}">2025</option>
        <!-- ... -->
    </select>
    <button type="submit">Filter</button>
</form>
```

#### Data Display:
```html
<tr th:each="rekap, iterStat : ${rekapList}">
    <td>
        <span th:if="${iterStat.index == 0}" class="badge rank-1">🥇 1</span>
        <span th:if="${iterStat.index == 1}" class="badge rank-2">🥈 2</span>
        <span th:if="${iterStat.index == 2}" class="badge rank-3">🥉 3</span>
        <span th:if="${iterStat.index >= 3}" th:text="${iterStat.index + 1}"></span>
    </td>
    <td th:text="${rekap.namaKaryawan}">Nama</td>
    <td th:text="${rekap.namaDivisi}">Divisi</td>
    <td th:text="${rekap.namaJabatan}">Jabatan</td>
    <td th:text="${rekap.tahun}">Tahun</td>
    <td>
        <strong th:text="${#numbers.formatDecimal(rekap.totalBobotTahunan, 1, 1)}">58.5</strong>
        <br>
        <small th:text="'(' + ${rekap.jumlahBulan} + ' bulan)'"> (12 bulan)</small>
    </td>
</tr>
```

## Cara Kerja Perhitungan

### Contoh Kasus:
Karyawan **Ahmad** dinilai selama 12 bulan di tahun 2026:

| Bulan | Total Nilai (Bobot) | Nilai Rata-Rata |
|-------|---------------------|-----------------|
| Jan   | 23                  | 4.6             |
| Feb   | 24                  | 4.8             |
| Mar   | 22                  | 4.4             |
| Apr   | 25                  | 5.0             |
| Mei   | 23                  | 4.6             |
| Jun   | 24                  | 4.8             |
| Jul   | 23                  | 4.6             |
| Agu   | 24                  | 4.8             |
| Sep   | 25                  | 5.0             |
| Okt   | 23                  | 4.6             |
| Nov   | 24                  | 4.8             |
| Des   | 25                  | 5.0             |

### Perhitungan:
- **Total Bobot Tahunan** = SUM(23+24+22+25+23+24+23+24+25+23+24+25) = **285**
- **Rata-Rata Tahunan** = AVG(4.6+4.8+4.4+5.0+4.6+4.8+4.6+4.8+5.0+4.6+4.8+5.0) = **4.75**
- **Jumlah Bulan** = COUNT(12) = **12 bulan**

### Karyawan dengan Bulan Tidak Lengkap:
Jika karyawan **Budi** hanya dinilai 6 bulan:

| Bulan | Total Nilai | Nilai Rata-Rata |
|-------|-------------|-----------------|
| Jan   | 20          | 4.0             |
| Feb   | 21          | 4.2             |
| Mar   | 20          | 4.0             |
| Apr   | 22          | 4.4             |
| Mei   | 21          | 4.2             |
| Jun   | 20          | 4.0             |

**Perhitungan:**
- **Total Bobot Tahunan** = SUM(20+21+20+22+21+20) = **124**
- **Rata-Rata Tahunan** = AVG(4.0+4.2+4.0+4.4+4.2+4.0) = **4.13**
- **Jumlah Bulan** = COUNT(6) = **6 bulan**

> **Note:** Budi akan kalah ranking dari Ahmad karena total bobotnya lebih kecil (124 vs 285), meskipun perhitungan fair karena sistem menampilkan jumlah bulan yang dinilai.

## Logika Ordering/Peringkat:

Query akan mengurutkan berdasarkan:
1. **SUM(p.totalNilai) DESC** - Total bobot tertinggi → Peringkat 1
2. **AVG(p.nilaiRataRata) DESC** - Jika total sama, urutkan berdasarkan rata-rata
3. **p.karyawan.namaKaryawan ASC** - Jika masih sama, urutkan berdasarkan nama A-Z

## URL Access:

- Default (tahun sekarang): `http://localhost:8031/penilaian_karyawan/rekaptahunan`
- Tahun 2025: `http://localhost:8031/penilaian_karyawan/rekaptahunan?tahun=2025`
- Tahun 2026: `http://localhost:8031/penilaian_karyawan/rekaptahunan?tahun=2026`

## Keunggulan Implementasi:

1. ✅ **Agregasi otomatis** menggunakan SQL GROUP BY
2. ✅ **Performance optimal** dengan single query menggunakan SUM, AVG, COUNT
3. ✅ **Filter dinamis** berdasarkan tahun
4. ✅ **Transparansi data** menampilkan jumlah bulan yang dinilai
5. ✅ **Ordering bertingkat** untuk handling nilai yang sama
6. ✅ **DTO clean** untuk transfer data ke view
7. ✅ **Badge peringkat** visual untuk top 3
8. ✅ **Responsive design** dengan DataTables
9. ✅ **Context path aware** menggunakan `@{/rekaptahunan}`
10. ✅ **Auto-submit** saat tahun berubah untuk UX yang lebih baik

