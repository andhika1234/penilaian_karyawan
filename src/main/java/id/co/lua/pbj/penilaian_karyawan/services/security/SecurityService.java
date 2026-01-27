package id.co.lua.pbj.penilaian_karyawan.services.security;


public interface SecurityService {
    String findLoggedInUsername();
    void loginByUsername(String username);
}
