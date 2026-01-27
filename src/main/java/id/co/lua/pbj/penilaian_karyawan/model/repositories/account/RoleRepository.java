package id.co.lua.pbj.penilaian_karyawan.model.repositories.account;

import id.co.lua.pbj.penilaian_karyawan.model.accounts.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}