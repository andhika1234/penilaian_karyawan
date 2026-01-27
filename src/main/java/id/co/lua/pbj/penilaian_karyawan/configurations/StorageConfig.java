package id.co.lua.pbj.penilaian_karyawan.configurations;

import id.co.lua.pbj.penilaian_karyawan.helpers.storages.StorageProperties;
import id.co.lua.pbj.penilaian_karyawan.helpers.storages.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class StorageConfig {
    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            // storageService.deleteAll();
            storageService.init();
        };
    }
}
