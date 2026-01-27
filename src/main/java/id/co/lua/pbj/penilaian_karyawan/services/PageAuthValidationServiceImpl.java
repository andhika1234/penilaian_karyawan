package id.co.lua.pbj.penilaian_karyawan.services;

import org.springframework.stereotype.Component;

@Component
public class PageAuthValidationServiceImpl implements PageAuthValdationService{
    @Override
    public boolean checkChangeAuthById(Long id, Long compareId) {
        if(id==compareId){
            return true;
        }
        return false;
    }
}
