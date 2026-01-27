package id.co.lua.pbj.penilaian_karyawan.model.accounts;


import id.co.lua.pbj.penilaian_karyawan.model.apps.AuditModel;
import id.co.lua.pbj.penilaian_karyawan.utils.MySanitizeString;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


import jakarta.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "role")
public class Role extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
    1	Administrator
    2	Admin Satker
    3   Satdik
    **/

    private String name;

    public void setName(String name) {
        this.name = MySanitizeString.strictSanitize(name);
    }

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "role")
    private List<RoleFeature> roleFeature;


}
