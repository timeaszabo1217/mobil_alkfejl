package com.example.furniturewebshop;

public class AdminManager {
    private static final String ADMIN_UID = "oykR3zykeJgY4ywY6u0KfomxrG12";

    public static boolean isAdmin(String uid) {
        return uid != null && uid.equals(ADMIN_UID);
    }
}

