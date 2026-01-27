package id.co.lua.pbj.penilaian_karyawan.model.accounts;

import id.co.lua.pbj.penilaian_karyawan.model.apps.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "users")
public class User extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String username;
    private String password;
    private String name;
    private String phone;
    private String cellular;

    @Column(columnDefinition="TEXT")
    private String address;

    private String email;
    private String nip;
    private String nik;
    private String jabatan;
    private String kodeWilayah;

    private String activationToken;
    @Column(name ="reset_password_token")
    private String resetPasswordToken;

    /*
    0: Inactive
    1: Active
    2: Banned
     */
    private Integer statusActivation;
    private String image;

    private UUID penggunaId;
    private String peranId;
    private String peran;
    private UUID ptkId;
    private Long rknId;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Role.class)
    @JoinColumn(name = "role_id")
    private Role role;

    public boolean isActive(){
        return statusActivation == 1;
    }
}