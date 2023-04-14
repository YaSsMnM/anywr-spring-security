package com.example.demo.sec.service;

import com.example.demo.sec.entities.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class UserDetailsServiceImpl {

    @Autowired
    private AccountService accountService;

    public UserDetailsService userDetailsService() {
        return username -> {
            AppUser appUser = accountService.loadUserByUserName(username);
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            appUser.getAppRole().forEach(r-> authorities.add(new SimpleGrantedAuthority(r.getRoleName())));
            return new User(appUser.getUserName(),appUser.getPassword(),authorities);
        };
    }
}
