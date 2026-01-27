# Penjelasan Query Pengumuman Peringkat

## Deskripsi
Query ini digunakan untuk menampilkan peringkat karyawan berdasarkan filter bulan dan tahun, dengan karyawan yang memiliki bobot (total nilai) paling tinggi berada di peringkat 1 dan yang terendah di peringkat paling bawah.

## Implementasi

### 1. Repository Layer (PenilaianKaryawanRepository.java)

#### Query utama untuk mendapatkan peringkat berdasarkan Bulan dan Tahun:
```java
@Query("SELECT p FROM PenilaianKaryawan p WHERE p.bulan = :bulan AND p.tahun = :tahun AND p.statusAktif = true " +
       "ORDER BY p.totalNilai DESC, p.nilaiRataRata DESC, p.karyawan.namaKaryawan ASC")
List<PenilaianKaryawan> findByBulanAndTahunOrderByTotalNilaiDesc(
    @Param("bulan") Integer bulan,
    @Param("tahun") Integer tahun
);
```

**Penjelasan:**
- `WHERE p.bulan = :bulan AND p.tahun = :tahun` - Filter berdasarkan bulan dan tahun yang dipilih
- `AND p.statusAktif = true` - Hanya menampilkan penilaian yang aktif
- `ORDER BY p.totalNilai DESC` - Urutkan berdasarkan total nilai (bobot) dari tertinggi ke terendah
- `p.nilaiRataRata DESC` - Jika total nilai sama, urutkan berdasarkan nilai rata-rata
- `p.karyawan.namaKaryawan ASC` - Jika masih sama, urutkan berdasarkan nama karyawan A-Z

#### Query alternatif untuk mendapatkan peringkat berdasarkan Tahun saja:
```java
@Query("SELECT p FROM PenilaianKaryawan p WHERE p.tahun = :tahun AND p.statusAktif = true " +
       "ORDER BY p.totalNilai DESC, p.nilaiRataRata DESC, p.karyawan.namaKaryawan ASC")
List<PenilaianKaryawan> findByTahunOrderByTotalNilaiDesc(@Param("tahun") Integer tahun);
```

### 2. Service Layer (PenilaianKaryawanService.java & PenilaianKaryawanServiceImpl.java)

#### Interface Method:
```java
/**
 * Get peringkat penilaian by bulan and tahun ordered by total bobot (nilai)
 * @param bulan Bulan
 * @param tahun Tahun
 * @return List of penilaian ordered by total nilai descending
 */
List<PenilaianKaryawan> getPeringkatByBulanAndTahun(Integer bulan, Integer tahun);
```

#### Implementation:
```java
@Override
@Transactional(readOnly = true)
public List<PenilaianKaryawan> getPeringkatByBulanAndTahun(Integer bulan, Integer tahun) {
    return penilaianKaryawanRepository.findByBulanAndTahunOrderByTotalNilaiDesc(bulan, tahun);
}
```

### 3. Controller Layer (PengumumanPeringkatController.java)

```java
@GetMapping("")
public ModelAndView index(ModelAndView mView,
                          @RequestParam(value = "bulan", required = false) Integer bulan,
                          @RequestParam(value = "tahun", required = false) Integer tahun,
                          @ModelAttribute(name = "resultCode") String resultCode,
                          @ModelAttribute(name = "resultMessage") String resultMessage) {
    
    // Set default to current month and year if not provided
    if (bulan == null || tahun == null) {
        LocalDate now = LocalDate.now();
        bulan = (bulan == null) ? now.getMonthValue() : bulan;
        tahun = (tahun == null) ? now.getYear() : tahun;
    }

    // Get ranking data ordered by total nilai (bobot) descending
    List<PenilaianKaryawan> peringkatList = penilaianKaryawanService.getPeringkatByBulanAndTahun(bulan, tahun);
    
    mView.addObject("peringkatList", peringkatList);
    mView.addObject("selectedBulan", bulan);
    mView.addObject("selectedTahun", tahun);
    mView.setViewName("pages/pengumumanperingkat/pengumumanperingkat-index");
    return mView;
}
```

**Penjelasan:**
- Menerima parameter `bulan` dan `tahun` dari request (optional)
- Jika tidak ada parameter, gunakan bulan dan tahun saat ini
- Memanggil service untuk mendapatkan data peringkat yang sudah terurut
- Mengirim data ke view untuk ditampilkan

### 4. View Layer (pengumumanperingkat-index.html)

#### Filter Form:
```html
<form method="get" action="/pengumumanperingkat" id="filterForm">
    <input type="month" id="filterBulan" name="filterMonth" />
    <input type="hidden" name="bulan" id="bulanInput" />
    <input type="hidden" name="tahun" id="tahunInput" />
    <button type="submit">Filter</button>
</form>
```

#### Data Display dengan Thymeleaf:
```html
<tbody>
    <tr th:each="penilaian, iterStat : ${peringkatList}">
        <td>
            <!-- Peringkat 1, 2, 3 dengan badge khusus -->
            <span th:if="${iterStat.index == 0}" class="badge rank-1">🥇 1</span>
            <span th:if="${iterStat.index == 1}" class="badge rank-2">🥈 2</span>
            <span th:if="${iterStat.index == 2}" class="badge rank-3">🥉 3</span>
            <span th:if="${iterStat.index >= 3}" th:text="${iterStat.index + 1}"></span>
        </td>
        <td th:text="${penilaian.karyawan.namaKaryawan}">Nama</td>
        <td th:text="${penilaian.divisi.namaDivisi}">Divisi</td>
        <td th:text="${penilaian.jabatan.namaJabatan}">Jabatan</td>
        <td>Bulan Tahun</td>
        <td th:text="${#numbers.formatDecimal(penilaian.nilaiRataRata, 1, 2)}">Nilai</td>
    </tr>
</tbody>
```

## Cara Kerja Perhitungan Bobot

### Entity PenilaianKaryawan.java memiliki method @PrePersist dan @PreUpdate:

```java
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
}
```

### Penjelasan Perhitungan:
1. **totalNilai** = Jumlah semua nilai dari setiap kriteria penilaian (BOBOT TOTAL)
2. **nilaiRataRata** = Total nilai dibagi jumlah kriteria
3. **kategoriPenilaian** = Kategori berdasarkan nilai rata-rata

## Contoh Perhitungan:

Jika seorang karyawan dinilai dengan 5 kriteria:
- Kriteria 1: 5
- Kriteria 2: 4
- Kriteria 3: 5
- Kriteria 4: 4
- Kriteria 5: 5

Maka:
- **totalNilai** = 5 + 4 + 5 + 4 + 5 = **23** (ini yang digunakan untuk ranking)
- **nilaiRataRata** = 23 / 5 = **4.6**
- **kategoriPenilaian** = "Sangat Baik" (karena 4.6 >= 4.5)

## URL Access:

- Default (bulan dan tahun sekarang): `http://localhost:8080/pengumumanperingkat`
- Dengan filter tertentu: `http://localhost:8080/pengumumanperingkat?bulan=1&tahun=2026`

## Keunggulan Implementasi:

1. ✅ Filter dinamis berdasarkan bulan dan tahun
2. ✅ Peringkat otomatis berdasarkan total bobot (totalNilai)
3. ✅ Hanya menampilkan data yang aktif (statusAktif = true)
4. ✅ Sorting bertingkat: total nilai → nilai rata-rata → nama
5. ✅ Default otomatis ke bulan dan tahun sekarang
6. ✅ UI yang menarik dengan badge khusus untuk peringkat 1, 2, 3
7. ✅ Responsive dan user-friendly dengan DataTables

