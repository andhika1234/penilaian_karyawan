package id.co.lua.pbj.penilaian_karyawan.services;


import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        if(SecurityContextHolder.getContext().getAuthentication() !=null){
            return Optional.of(SecurityContextHolder.getContext().getAuthentication().getName());
        }else{
            return Optional.of("SYSTEM");
        }
        // Use below commented code when will use Spring Security.
    }
}