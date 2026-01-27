package id.co.lua.pbj.penilaian_karyawan.configurations;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);         // jumlah thread minimum yang aktif terus
        executor.setMaxPoolSize(20);         // jumlah maksimal thread paralel
        executor.setQueueCapacity(200);      // antrian jika semua thread sibuk
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }

}
