package id.co.lua.pbj.penilaian_karyawan.model.accounts;

import id.co.lua.pbj.penilaian_karyawan.controller.apps.Feature;
import id.co.lua.pbj.penilaian_karyawan.model.apps.AuditModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import jakarta.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "role_feature")
public class RoleFeature extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Role.class)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Feature.class)
    @JoinColumn(name = "feature_id")
    private Feature feature;
}
