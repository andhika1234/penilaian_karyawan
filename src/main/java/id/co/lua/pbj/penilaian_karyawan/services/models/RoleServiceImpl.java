package id.co.lua.pbj.penilaian_karyawan.services.models;

import id.co.lua.pbj.penilaian_karyawan.model.accounts.Role;

import id.co.lua.pbj.penilaian_karyawan.model.repositories.account.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;



@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;


    @Override
    public Role getById(Long id) {
        Optional<Role> role = roleRepository.findById(id);
        if(role.isPresent()){
            return role.get();
        }
        return null;
    }


}
