package id.co.lua.pbj.penilaian_karyawan.services.models;

import id.co.lua.pbj.penilaian_karyawan.exceptions.IdNotExistsException;
import id.co.lua.pbj.penilaian_karyawan.exceptions.ResourceNotFoundException;
import id.co.lua.pbj.penilaian_karyawan.model.accounts.RoleFeature;
import id.co.lua.pbj.penilaian_karyawan.model.accounts.User;
import id.co.lua.pbj.penilaian_karyawan.model.repositories.account.UserRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServicesImpl implements UserServices {
    private static final Logger LOG = LogManager.getLogger(UserServicesImpl.class);



    @Autowired
    UserRepository userRepository;


    @Autowired
    RoleService roleService;

    @Autowired
    PasswordEncoder pEncoder;

    @Override
    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()){
            return userOptional.get();
        }
        throw new IdNotExistsException();
    }


    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }



    @Override
    public Set<GrantedAuthority> grantedAuthorities(String username) {
        User user = userRepository.findByUsername(username);

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        if(user.isActive()){
            for(RoleFeature roleFeature:user.getRole().getRoleFeature()){
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + roleFeature.getFeature().getName()));
            }
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_"+user.getRole().getName()));
        }else{
            //TODO: Berhasil login tapi ada notifikasi status akunnya sudah tidak aktif.
        }
        return grantedAuthorities;
    }

    @Override
    public User create(User user) {
        user.setPassword(pEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User update(User userUpdate, User user) {
        userUpdate.setName(user.getName())
                .setEmail(user.getEmail())
                .setUsername(user.getUsername())
                .setName(user.getName())
                .setRole(user.getRole());
        return userRepository.save(userUpdate);
    }



    @Override
    public User getByUserByPenggunaId(UUID penggunaId) {
        return userRepository.findByPenggunaId(penggunaId);
    }

    @Override
    public User activate(String token) {
        User user = getUserByToken(token);
        user.setStatusActivation(1);
        user.setActivationToken(null);
        return userRepository.save(user);
    }



    @Override
    public User getUserByToken(String token) {
        Optional<User> userOptional = userRepository.findUserByActivationToken(token);
        if(userOptional.isPresent()){
            return userOptional.get();
        }
        throw new ResourceNotFoundException("User activation token not found");
    }


}
