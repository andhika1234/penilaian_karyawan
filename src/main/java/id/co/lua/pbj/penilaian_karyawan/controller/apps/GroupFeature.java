package id.co.lua.pbj.penilaian_karyawan.controller.apps;

import id.co.lua.pbj.penilaian_karyawan.model.apps.AuditModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "group_feature")
public class GroupFeature extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String groups;

}
