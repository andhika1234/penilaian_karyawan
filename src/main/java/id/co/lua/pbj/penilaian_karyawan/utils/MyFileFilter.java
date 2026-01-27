package id.co.lua.pbj.penilaian_karyawan.utils;

import id.co.lua.pbj.penilaian_karyawan.model.enumfilter.EnumMagicNumberFile;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class MyFileFilter {
    private MyFileFilter() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static boolean fileExecuteNotAccepted(MultipartFile file) throws IOException {
        return EnumMagicNumberFile.MagicBytesNotAccept.EXE.is(file.getInputStream()) ||
                EnumMagicNumberFile.MagicBytesNotAccept.BIN.is(file.getInputStream());
    }

    public static boolean fileAccepted(MultipartFile file) throws IOException {
        return EnumMagicNumberFile.MagicBytesAccept.JPG.is(file.getInputStream()) ||
                EnumMagicNumberFile.MagicBytesAccept.PNG.is(file.getInputStream()) ||
                EnumMagicNumberFile.MagicBytesAccept.PDF.is(file.getInputStream());
    }

    public static String convertMimeToExtension(String mime) throws MimeTypeException {
        MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
        MimeType fileType = allTypes.forName(mime);
        String ext = null;
        if(fileType!=null){
            ext = fileType.getExtension();
        }
        return ext;
    }
}
