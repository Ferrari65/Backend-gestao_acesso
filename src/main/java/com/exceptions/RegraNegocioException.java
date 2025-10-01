package com.exceptions;

public class RegraNegocioException extends RuntimeException {
  private final String code;

  public RegraNegocioException(String message) {super(message);this.code = "REGRA_NEGOCIO";}

  public RegraNegocioException(String code, String message) {super(message);this.code = code;}
  public String getCode() { return code; }

}