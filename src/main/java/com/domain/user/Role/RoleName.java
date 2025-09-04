package com.domain.user.Role;

public enum RoleName {
    GESTOR,LIDER, COLABORADOR;

    public static RoleName from (String value){
        if (value == null) throw new IllegalArgumentException("Role VAZIA");
        return RoleName.valueOf(value.trim().toUpperCase());
    }
}
