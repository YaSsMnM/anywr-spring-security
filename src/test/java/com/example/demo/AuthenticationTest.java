package com.example.demo;

import com.example.demo.sec.entities.AppRole;
import com.example.demo.sec.entities.AppUser;
import com.example.demo.sec.service.AccountService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

class AuthenticationTest {
    @Mock
    private AccountService accountService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void testAuthentication() {
        MockitoAnnotations.initMocks(this);

        // Set up test data
        AppUser appUser = new AppUser(1L,"user1","12345","user1@gmail.com",null);
        appUser.setAppRole(Collections.singleton(new AppRole(1L,"USER")));
        Mockito.when(accountService.loadUserByUserName("user1")).thenReturn(appUser);
        Mockito.when(passwordEncoder.encode("12345")).thenReturn("12345");
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken("user1", "12345",authorities);
        Mockito.when(authenticationManager.authenticate(authRequest)).thenReturn(authRequest);
        Authentication authResult = authenticationManager.authenticate(authRequest);
        assertTrue(authResult.isAuthenticated());
        assertEquals(appUser.getUserName(),authResult.getPrincipal());
        assertEquals("12345", authResult.getCredentials());
        authorities = (Collection<GrantedAuthority>) authResult.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("USER")));

    }
}
