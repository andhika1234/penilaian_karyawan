package id.co.lua.pbj.penilaian_karyawan.helpers.storages;

import org.apache.tika.mime.MimeTypeException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    void store(MultipartFile file, String folder, String filename);

    Stream<Path> loadAll();

    Path load(String filename);

    Path rootLocation();

    Resource loadAsResource(String filename);

    void deleteAll();

    void delete(String filename);

    String getExtensionFromMime(String mime) throws MimeTypeException;

    List<String> getListExtensionFromMime(String[] mime) throws MimeTypeException;

    boolean isAllowFile(String filename,List<String> extList);

}
