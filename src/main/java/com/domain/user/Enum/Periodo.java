package com.domain.user.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Periodo {
    MANHA, TARDE, NOITE, MADRUGADA;

    @JsonCreator
    public static  Periodo from (String value){
        if (value == null ) return null;
        return  Periodo.valueOf(value.trim().toUpperCase());
    }

    @JsonValue
    public String toValue(){
        return name();
    }
}