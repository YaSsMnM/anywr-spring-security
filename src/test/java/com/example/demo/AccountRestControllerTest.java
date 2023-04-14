package com.example.demo;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collection;

import com.example.demo.sec.model.RoleUserFrom;
import com.example.demo.sec.web.AccountRestController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.junit.Assert.assertEquals;

import com.example.demo.sec.entities.AppRole;
import com.example.demo.sec.entities.AppUser;
import com.example.demo.sec.service.AccountService;

class AccountRestControllerTest {

    private AccountRestController controller;

    @Mock
    private AccountService accountService;

    @BeforeEach
    void setUp() throws Exception {
        accountService = mock(AccountService.class);
        controller = new AccountRestController(accountService);
    }

    @Test
    void testRegister() {
        Collection<AppRole> appRoles = new ArrayList<>();
        appRoles.add(accountService.getRole("USER"));
        AppUser user = new AppUser(null, "user1", "12345", "user1@outlook.com", appRoles);

        ResponseEntity<String> result = controller.register(user);

        assertEquals(ResponseEntity.ok("User registered successfully"), result);
        verify(accountService, times(1)).saveUser(user);
    }

    @Test
    void testRetrieveProfilInfo() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user1");

        Collection<AppRole> appRoles = new ArrayList<>();
        appRoles.add(accountService.getRole("USER"));
        AppUser user = new AppUser(null, "user1", "12345", "user1@outlook.com", appRoles);
        when(accountService.loadUserByUserName("user1")).thenReturn(user);

        AppUser result = controller.retrieveProfilInfo(auth);

        assertEquals(user, result);
        verify(accountService, times(1)).loadUserByUserName("user1");
    }

    @Test
    void testUpdateProfil() {
        Long id = 1L;
        AppUser user = new AppUser(id, "user1", "12345", "user1@outlook.com", null);
        AppUser updatedUser = new AppUser(id, "newuser1", "new12345", "newuser1@outlook.com", null);

        when(accountService.findById(id)).thenReturn(java.util.Optional.of(user));
        when(accountService.saveUser(user)).thenReturn(updatedUser);

        AppUser result = controller.updateProfil(id, updatedUser);

        assertEquals(updatedUser, result);
        verify(accountService, times(1)).findById(id);
        verify(accountService, times(1)).saveUser(user);
    }

    @Test
    void testSaveRoles() {
        AppRole appRole = new AppRole(null,"USER");
        when(accountService.addNewRole(appRole)).thenReturn(appRole);
        AppRole result = controller.saveRoles(appRole);

        assertEquals(appRole, result);
        verify(accountService, times(1)).addNewRole(appRole);
    }

    @Test
    void testAddRoleToUser() {
        RoleUserFrom roleUserFrom = new RoleUserFrom("user1","USER");
        controller.addRoleToUser(roleUserFrom);

        verify(accountService, times(1)).addRoleToUser("user1", "USER");
    }
}
