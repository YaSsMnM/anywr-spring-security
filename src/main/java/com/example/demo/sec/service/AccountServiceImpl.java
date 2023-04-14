package com.example.demo.sec.service;

import com.example.demo.sec.entities.AppRole;
import com.example.demo.sec.entities.AppUser;
import com.example.demo.sec.repo.AppRoleRepository;
import com.example.demo.sec.repo.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {
    private AppUserRepository appUserRepository;
    private AppRoleRepository appRoleRepository;

    private PasswordEncoder encoder;

    public AccountServiceImpl(AppUserRepository appUserRepository, AppRoleRepository appRoleRepository, PasswordEncoder encoder) {
        this.appUserRepository = appUserRepository;
        this.appRoleRepository = appRoleRepository;
        this.encoder = encoder;
    }

    @Override
    public AppUser saveUser(AppUser appUser) {
        String pw = appUser.getPassword();
        appUser.setPassword(encoder.encode(pw));
        return appUserRepository.save(appUser);
    }

    @Override
    public AppRole addNewRole(AppRole appRole) {
        return appRoleRepository.save(appRole);
    }

    @Override
    public void addRoleToUser(String userName, String roleName) {
        AppUser appUser = appUserRepository.findByUserName(userName);
        AppRole appRole = getRole(roleName);
        appUser.getAppRole().add(appRole);
    }

    @Override
    public AppUser loadUserByUserName(String userName) {
        return appUserRepository.findByUserName(userName);
    }

    @Override
    public Optional<AppUser> findById(Long id) {
        return appUserRepository.findById(id);
    }

    @Override
    public AppRole getRole(String roleName) {
        return appRoleRepository.findByRoleName(roleName);
    }
}
