package com.mihalsky.kitchen.constant;

public class SecurityConstant {
  public static final long EXPIRATION_TIME = 432_000_000;
  public static final String TOKEN_PREFIX = "Bearer ";
  public static final String JWT_TOKEN_HEADER = "Jwt-Token";
  public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
  public static final String LUV_2_CODE_LLC = "Luv2code, LLC";
  public static final String LUV_2_CODE_ADMINISTRATION = "User Managment Portal";
  public static final String AUTHORITIES = "Authorities";
  public static final String FORBIDDEN_MESSAGE = "You need to log in to access this page";
  public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page";
  public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
  public static final String[] PUBLIC_URLS = {"/user/login","/user/register","/user/reserpassword/**","/user/image/**","/food/image/**"};
  //public static final String[] PUBLIC_URLS = {"**"};
}
