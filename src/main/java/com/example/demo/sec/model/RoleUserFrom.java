package com.example.demo.sec.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data @AllArgsConstructor
public class RoleUserFrom {
    @NotNull
    @Size(min = 5, max = 50)
    private String userName;
    @NotNull
    @Size(min = 4, max = 5)
    private String roleName;
}