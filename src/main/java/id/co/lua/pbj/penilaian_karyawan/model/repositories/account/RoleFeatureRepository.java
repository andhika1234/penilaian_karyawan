package id.co.lua.pbj.penilaian_karyawan.model.repositories.account;

import id.co.lua.pbj.penilaian_karyawan.controller.apps.Feature;
import id.co.lua.pbj.penilaian_karyawan.model.accounts.Role;
import id.co.lua.pbj.penilaian_karyawan.model.accounts.RoleFeature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleFeatureRepository extends JpaRepository<RoleFeature, Long> {
    List<RoleFeature> getByFeature(Feature feature);
    RoleFeature findByRoleAndFeature(Role role, Feature feature);
}