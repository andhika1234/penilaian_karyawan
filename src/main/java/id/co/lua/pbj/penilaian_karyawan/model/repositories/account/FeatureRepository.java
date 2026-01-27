package id.co.lua.pbj.penilaian_karyawan.model.repositories.account;

import id.co.lua.pbj.penilaian_karyawan.controller.apps.Feature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeatureRepository extends JpaRepository<Feature, Long> {
    List<Feature> findByOrderByGroupFeatureAsc();


}