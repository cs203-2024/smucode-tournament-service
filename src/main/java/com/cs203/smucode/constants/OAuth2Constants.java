package com.cs203.smucode.constants;

public final class OAuth2Constants {
    public static final String SCOPE = "scope";
    public static final String SUBJECT = "sub";
    public static final String ISSUER = "iss";

    private OAuth2Constants() {
        throw new IllegalStateException("Constants class");
    }
}
