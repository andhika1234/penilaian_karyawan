package id.co.lua.pbj.penilaian_karyawan.model.repositories.account;

import id.co.lua.pbj.penilaian_karyawan.model.accounts.Role;
import id.co.lua.pbj.penilaian_karyawan.model.accounts.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByEmail(String email);

    User findByEmailAndStatusActivation(String email, Integer statusActivation);

    User findByResetPasswordToken(String token);

//    Optional<User> findByResetEmail(String email);
//    Optional<User> findByResetToken(String token);

    Optional<User> findUserByResetPasswordToken(String token);

    Optional<User> findUserByPassword(String password);

    Optional<User> findUserByActivationToken(String token);

    List<User> findUserByRole(Role role);

    @Query(value="select coalesce(count(id),0) as jumEmail from users where " +
            " email = ?1",nativeQuery = true)
    int getSumEmailRegistered(String email);

    @Query(value="select coalesce(count(id),0) as jumUsername from users where " +
            " username = ?1",nativeQuery = true)
    int getSumUsernameRegistered(String username);

    User findByPenggunaId(UUID penggunaId);

}