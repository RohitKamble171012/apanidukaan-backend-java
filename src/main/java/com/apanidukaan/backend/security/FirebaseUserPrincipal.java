package com.apanidukaan.backend.security;

import lombok.Getter;

@Getter
public class FirebaseUserPrincipal {

    private final String uid;
    private final String email;

    public FirebaseUserPrincipal(String uid, String email) {
        this.uid = uid;
        this.email = email;
    }
}