package id.co.lua.pbj.penilaian_karyawan.services.models;


import id.co.lua.pbj.penilaian_karyawan.model.accounts.RoleFeature;
import id.co.lua.pbj.penilaian_karyawan.model.accounts.User;
import id.co.lua.pbj.penilaian_karyawan.model.repositories.account.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    private UserRepository userRepository;

    public UserDetailServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) userRepository.findByEmail(username);

        if (user == null) throw new UsernameNotFoundException(username);

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        if(user.isActive()){
            for(RoleFeature roleFeature:user.getRole().getRoleFeature()){
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + roleFeature.getFeature().getName()));
            }
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_"+user.getRole().getName()));
        }else{
            //TODO: Berhasil login tapi ada notifikasi status akunnya sudah tidak aktif.
            //sementara set user or password null agar tidak bisa login

        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);


    }
}
