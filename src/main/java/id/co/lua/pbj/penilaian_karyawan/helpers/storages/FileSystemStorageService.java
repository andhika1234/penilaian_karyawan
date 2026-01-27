package id.co.lua.pbj.penilaian_karyawan.helpers.storages;


import id.co.lua.pbj.penilaian_karyawan.utils.MyFileFilter;
import org.apache.tika.mime.MimeTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void store(MultipartFile file, String folder, String file_name) {
        String filename = StringUtils.cleanPath(folder + "/" + file_name);
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                File f = new File(this.rootLocation.resolve(folder).toUri());
                if (!f.exists()) {
                    f.mkdir();
                }
                Files.copy(inputStream, this.rootLocation.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Path rootLocation() {
        return rootLocation;
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void delete(String filename) {
        try {
            Path file = load(filename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            // KEEP GOING WE JUST KEEP TRACK ON THE EXCEPTION
            e.printStackTrace();
        }
    }

    @Override
    public String getExtensionFromMime(String mime) throws MimeTypeException {
        String ext = MyFileFilter.convertMimeToExtension(mime);
        return ext;
    }

    @Override
    public List<String> getListExtensionFromMime(String[] mime) throws MimeTypeException {
        List<String> list = null;
        if(mime!=null){
            list = new ArrayList<>();
            for(int i=0;i<mime.length;i++) {
                String ext = MyFileFilter.convertMimeToExtension(mime[i]);
                list.add(ext);
            }
        }
        return list;
    }

    @Override
    public boolean isAllowFile(String filename, List<String> extList) {
        String extFile = filename.substring(filename.lastIndexOf('.') + 1, filename.length());
        for(String s:extList){
            if(s.equalsIgnoreCase("."+extFile)){
                return true;
            }

        }
        return false;
    }

    @Override
    public void init() {
        try {
            if (!new File(rootLocation.toUri()).exists()) {
                Files.createDirectories(rootLocation);
            }
        } catch (FileAlreadyExistsException fe) {
            // It's fine
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
