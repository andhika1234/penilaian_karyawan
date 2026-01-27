package id.co.lua.pbj.penilaian_karyawan.services.models;

import id.co.lua.pbj.penilaian_karyawan.model.accounts.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserServices {

    List<User> getAllUser();

    User getUserById(Long id);

    User getUserByUsername(String username);

    User getUserByEmail(String email);


    Set<GrantedAuthority> grantedAuthorities(String username);

    User create(User user);

    User update(User userUpdate, User user);



    User getByUserByPenggunaId(UUID penggunaId);


    User activate(String token);


    User getUserByToken(String token);


}
