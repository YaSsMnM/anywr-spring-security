package com.example.demo.sec.service;

import com.example.demo.sec.entities.AppRole;
import com.example.demo.sec.entities.AppUser;

import java.util.Optional;

public interface AccountService {
    AppUser saveUser(AppUser appUser);
    AppRole addNewRole(AppRole appRole);
    void addRoleToUser(String userName,String roleName);
    AppUser loadUserByUserName(String userName);
    Optional<AppUser> findById(Long id);
    AppRole getRole(String roleName);
}
