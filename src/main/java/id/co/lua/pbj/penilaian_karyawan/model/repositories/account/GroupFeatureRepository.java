package id.co.lua.pbj.penilaian_karyawan.model.repositories.account;

import id.co.lua.pbj.penilaian_karyawan.controller.apps.GroupFeature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupFeatureRepository extends JpaRepository<GroupFeature, Long> {
    List<GroupFeature> findByOrderByIdAsc();


}