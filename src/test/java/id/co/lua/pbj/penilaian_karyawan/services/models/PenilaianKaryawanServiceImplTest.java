package id.co.lua.pbj.penilaian_karyawan.services.models;

import id.co.lua.pbj.penilaian_karyawan.model.apps.Divisi;
import id.co.lua.pbj.penilaian_karyawan.model.apps.Jabatan;
import id.co.lua.pbj.penilaian_karyawan.model.apps.Karyawan;
import id.co.lua.pbj.penilaian_karyawan.model.apps.PenilaianKaryawan;
import id.co.lua.pbj.penilaian_karyawan.model.repositories.apps.KaryawanRepository;
import id.co.lua.pbj.penilaian_karyawan.model.repositories.apps.PenilaianKaryawanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PenilaianKaryawanServiceImplTest {

    @Mock
    private PenilaianKaryawanRepository penilaianKaryawanRepository;

    @Mock
    private KaryawanRepository karyawanRepository;

    @InjectMocks
    private PenilaianKaryawanServiceImpl penilaianKaryawanService;

    private Karyawan karyawan;
    private PenilaianKaryawan penilaian;

    @BeforeEach
    void setUp() {
        // Setup dummy data
        Divisi divisi = new Divisi();
        divisi.setId(1L);
        divisi.setNamaDivisi("IT Department");

        Jabatan jabatan = new Jabatan();
        jabatan.setId(1L);
        jabatan.setNamaJabatan("Software Developer");

        karyawan = new Karyawan();
        karyawan.setId(1L);
        karyawan.setNamaKaryawan("John Doe");
        karyawan.setNik("1234567890123456");
        karyawan.setEmailKaryawan("john.doe@example.com");
        karyawan.setNomorTelepon("08123456789");
        karyawan.setDivisi(divisi);
        karyawan.setJabatan(jabatan);
        karyawan.setStatusAktif(true);

        penilaian = new PenilaianKaryawan();
        penilaian.setId(1L);
        penilaian.setKaryawan(karyawan);
        penilaian.setBulan(1);
        penilaian.setTahun(2024);
        penilaian.setNilaiTanggungJawab(5);
        penilaian.setNilaiKeterlambatan(4);
        penilaian.setNilaiDisiplin(5);
        penilaian.setNilaiKualitasKerja(4);
        penilaian.setNilaiKerjaSama(5);
        penilaian.setStatusAktif(true);
        penilaian.setTanggalPenilaian(LocalDate.now());
    }

    @Test
    void testGetAllActivePenilaian() {
        // Given
        List<PenilaianKaryawan> expectedList = Arrays.asList(penilaian);
        when(penilaianKaryawanRepository.findAllActivePenilaian()).thenReturn(expectedList);

        // When
        List<PenilaianKaryawan> result = penilaianKaryawanService.getAllActivePenilaian();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(penilaian.getId(), result.get(0).getId());
        verify(penilaianKaryawanRepository, times(1)).findAllActivePenilaian();
    }

    @Test
    void testGetPenilaianById_Success() {
        // Given
        when(penilaianKaryawanRepository.findById(1L)).thenReturn(Optional.of(penilaian));

        // When
        Optional<PenilaianKaryawan> result = penilaianKaryawanService.getPenilaianById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(penilaian.getId(), result.get().getId());
        verify(penilaianKaryawanRepository, times(1)).findById(1L);
    }

    @Test
    void testSavePenilaian_Success() {
        // Given
        when(penilaianKaryawanRepository.countByKaryawanAndBulanAndTahun(anyLong(), anyInt(), anyInt()))
            .thenReturn(0);
        when(penilaianKaryawanRepository.save(any(PenilaianKaryawan.class)))
            .thenReturn(penilaian);

        // When
        PenilaianKaryawan result = penilaianKaryawanService.savePenilaian(penilaian);

        // Then
        assertNotNull(result);
        assertEquals(penilaian.getId(), result.getId());
        verify(penilaianKaryawanRepository, times(1)).save(any(PenilaianKaryawan.class));
    }

    @Test
    void testSavePenilaian_DuplicateError() {
        // Given
        when(penilaianKaryawanRepository.countByKaryawanAndBulanAndTahun(anyLong(), anyInt(), anyInt()))
            .thenReturn(1);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> penilaianKaryawanService.savePenilaian(penilaian)
        );

        assertTrue(exception.getMessage().contains("sudah ada"));
        verify(penilaianKaryawanRepository, never()).save(any(PenilaianKaryawan.class));
    }

    @Test
    void testSavePenilaian_InvalidBulan() {
        // Given
        penilaian.setBulan(13); // Invalid month

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> penilaianKaryawanService.savePenilaian(penilaian)
        );

        assertTrue(exception.getMessage().contains("Bulan harus antara 1-12"));
    }

    @Test
    void testUpdatePenilaian_Success() {
        // Given
        PenilaianKaryawan updatedPenilaian = new PenilaianKaryawan();
        updatedPenilaian.setKaryawan(karyawan);
        updatedPenilaian.setBulan(1);
        updatedPenilaian.setTahun(2024);
        updatedPenilaian.setNilaiTanggungJawab(3);
        updatedPenilaian.setNilaiKeterlambatan(3);
        updatedPenilaian.setNilaiDisiplin(3);
        updatedPenilaian.setNilaiKualitasKerja(3);
        updatedPenilaian.setNilaiKerjaSama(3);

        when(penilaianKaryawanRepository.findById(1L)).thenReturn(Optional.of(penilaian));
        when(penilaianKaryawanRepository.countByKaryawanAndBulanAndTahunExcludingId(
            anyLong(), anyInt(), anyInt(), anyLong())).thenReturn(0);
        when(penilaianKaryawanRepository.save(any(PenilaianKaryawan.class)))
            .thenReturn(penilaian);

        // When
        PenilaianKaryawan result = penilaianKaryawanService.updatePenilaian(1L, updatedPenilaian);

        // Then
        assertNotNull(result);
        verify(penilaianKaryawanRepository, times(1)).save(any(PenilaianKaryawan.class));
    }

    @Test
    void testUpdatePenilaian_NotFound() {
        // Given
        when(penilaianKaryawanRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> penilaianKaryawanService.updatePenilaian(999L, penilaian)
        );

        assertTrue(exception.getMessage().contains("tidak ditemukan"));
    }

    @Test
    void testDeletePenilaian_Success() {
        // Given
        when(penilaianKaryawanRepository.findById(1L)).thenReturn(Optional.of(penilaian));
        when(penilaianKaryawanRepository.save(any(PenilaianKaryawan.class)))
            .thenReturn(penilaian);

        // When
        penilaianKaryawanService.deletePenilaian(1L);

        // Then
        verify(penilaianKaryawanRepository, times(1)).save(any(PenilaianKaryawan.class));
    }

    @Test
    void testPermanentDeletePenilaian_Success() {
        // Given
        when(penilaianKaryawanRepository.findById(1L)).thenReturn(Optional.of(penilaian));

        // When
        penilaianKaryawanService.permanentDeletePenilaian(1L);

        // Then
        verify(penilaianKaryawanRepository, times(1)).deleteById(1L);
    }

    @Test
    void testActivatePenilaian_Success() {
        // Given
        penilaian.setStatusAktif(false);
        when(penilaianKaryawanRepository.findById(1L)).thenReturn(Optional.of(penilaian));
        when(penilaianKaryawanRepository.save(any(PenilaianKaryawan.class)))
            .thenReturn(penilaian);

        // When
        penilaianKaryawanService.activatePenilaian(1L);

        // Then
        verify(penilaianKaryawanRepository, times(1)).save(any(PenilaianKaryawan.class));
    }

    @Test
    void testDeactivatePenilaian_Success() {
        // Given
        when(penilaianKaryawanRepository.findById(1L)).thenReturn(Optional.of(penilaian));
        when(penilaianKaryawanRepository.save(any(PenilaianKaryawan.class)))
            .thenReturn(penilaian);

        // When
        penilaianKaryawanService.deactivatePenilaian(1L);

        // Then
        verify(penilaianKaryawanRepository, times(1)).save(any(PenilaianKaryawan.class));
    }

    @Test
    void testSearchPenilaian() {
        // Given
        List<PenilaianKaryawan> expectedList = Arrays.asList(penilaian);
        when(penilaianKaryawanRepository.searchPenilaian("John")).thenReturn(expectedList);

        // When
        List<PenilaianKaryawan> result = penilaianKaryawanService.searchPenilaian("John");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(penilaianKaryawanRepository, times(1)).searchPenilaian("John");
    }

    @Test
    void testGetPenilaianByBulanAndTahun() {
        // Given
        List<PenilaianKaryawan> expectedList = Arrays.asList(penilaian);
        when(penilaianKaryawanRepository.findByBulanAndTahun(1, 2024))
            .thenReturn(expectedList);

        // When
        List<PenilaianKaryawan> result = penilaianKaryawanService.getPenilaianByBulanAndTahun(1, 2024);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(penilaianKaryawanRepository, times(1)).findByBulanAndTahun(1, 2024);
    }

    @Test
    void testGetAverageNilaiByKaryawan() {
        // Given
        when(penilaianKaryawanRepository.getAverageNilaiByKaryawan(1L))
            .thenReturn(4.5);

        // When
        Double result = penilaianKaryawanService.getAverageNilaiByKaryawan(1L);

        // Then
        assertNotNull(result);
        assertEquals(4.5, result);
        verify(penilaianKaryawanRepository, times(1)).getAverageNilaiByKaryawan(1L);
    }

    @Test
    void testIsPenilaianExists() {
        // Given
        when(penilaianKaryawanRepository.countByKaryawanAndBulanAndTahun(1L, 1, 2024))
            .thenReturn(1);

        // When
        boolean result = penilaianKaryawanService.isPenilaianExists(1L, 1, 2024);

        // Then
        assertTrue(result);
        verify(penilaianKaryawanRepository, times(1))
            .countByKaryawanAndBulanAndTahun(1L, 1, 2024);
    }
}

