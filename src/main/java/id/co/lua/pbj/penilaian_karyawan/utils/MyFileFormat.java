package id.co.lua.pbj.penilaian_karyawan.utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.multipart.MultipartFile;

public class MyFileFormat {
    private MyFileFormat() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String fileNameRandom(MultipartFile file){
        return String.format("%s.%s", RandomStringUtils.randomAlphanumeric(16), FilenameUtils.getExtension(file.getOriginalFilename()));
    }
}
