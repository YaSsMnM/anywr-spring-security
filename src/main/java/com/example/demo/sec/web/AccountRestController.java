package com.example.demo.sec.web;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.sec.JWTUtil;
import com.example.demo.sec.entities.AppRole;
import com.example.demo.sec.entities.AppUser;
import com.example.demo.sec.model.RoleUserFrom;
import com.example.demo.sec.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class AccountRestController {
    private AccountService accountService;

    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<String> register(@Valid @RequestBody AppUser user) {
        Collection<AppRole> appRoles = new ArrayList<>();
        appRoles.add(accountService.getRole("USER"));
        user.setAppRole(appRoles);
        accountService.saveUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping(path = "/retrieveProfil")
    public AppUser retrieveProfilInfo(Authentication authentication) {
        String currentUsername = authentication.getName();
        return accountService.loadUserByUserName(currentUsername);
    }

    @PutMapping(path = "/updateProfil/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public AppUser updateProfil(@PathVariable Long id, @Valid @RequestBody AppUser user) {
        AppUser appUser = accountService.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        appUser.setUserName(user.getUserName());
        appUser.setPassword(user.getPassword());
        appUser.setEmail(user.getEmail());
        appUser.setAppRole(user.getAppRole());

        return accountService.saveUser(appUser);
    }

    @PostMapping(path = "/roles")
    @PreAuthorize("hasAuthority('ADMIN')")
    public AppRole saveRoles(@Valid @RequestBody AppRole appRole) {
        return accountService.addNewRole(appRole);
    }

    @PostMapping(path = "/addRoleToUser")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void addRoleToUser(@Valid @RequestBody RoleUserFrom roleUserFrom) {
        accountService.addRoleToUser(roleUserFrom.getUserName(), roleUserFrom.getRoleName());
    }

    @GetMapping(path = "/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authToken = request.getHeader(JWTUtil.AUTH_HEADER);
        if (authToken != null && authToken.startsWith(JWTUtil.PREFIX)) {
            try {
                String jwt = authToken.substring(7);
                Algorithm algorithm = Algorithm.HMAC256(JWTUtil.SECRET);
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(jwt);
                String userName = decodedJWT.getSubject();
                AppUser appUser = accountService.loadUserByUserName(userName);
                String jwtAccessToken = JWT.create().withSubject(appUser.getUserName())
                        .withExpiresAt(new Date(System.currentTimeMillis() + JWTUtil.EXPIRE_ACCESS_TOKEN))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", appUser.getAppRole().stream().map(AppRole::getRoleName).collect(Collectors.toList()))
                        .sign(algorithm);
                Map<String, String> idToken = new HashMap<>();
                idToken.put("access-token", jwtAccessToken);
                idToken.put("refresh-token", jwt);
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), idToken);
            } catch (Exception e) {
                throw e;
            }
        } else {
            throw new RuntimeException("Refresh token required!!");
        }
    }
}