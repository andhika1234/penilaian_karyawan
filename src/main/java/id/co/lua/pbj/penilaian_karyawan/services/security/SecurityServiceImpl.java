package id.co.lua.pbj.penilaian_karyawan.services.security;

import id.co.lua.pbj.penilaian_karyawan.model.accounts.User;
import id.co.lua.pbj.penilaian_karyawan.services.models.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SecurityServiceImpl implements SecurityService {

    @Qualifier("userDetailServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;

    private UserServices userServices;

    @Autowired
    public void setUserServices(UserServices userServices) {
        this.userServices = userServices;
    }

    @Override
    public String findLoggedInUsername() {
        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getDetails();
        if (userDetails instanceof UserDetails) {
            return ((UserDetails) userDetails).getUsername();
        }
        return null;
    }



    @Override
    public void loginByUsername(String username) {

        Set<GrantedAuthority> grantedAuthorities = userServices.grantedAuthorities(username);

        User user = userServices.getUserByUsername(username);

        org.springframework.security.core.userdetails.User userPrincipal = new org.springframework.security.core.userdetails.User(username, user.getPassword(), true, true, true, true, grantedAuthorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, grantedAuthorities);

        if (authentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }


}
