package com.lifelink.util;

import java.util.List;
import java.util.regex.Pattern;

/** Values shared by several classes. */
public final class AppConstants {
    private AppConstants() { }

    public static final List<String> BLOOD_GROUPS =
            List.of("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");

    public static final String SESSION_USER = "user";
    public static final String REMEMBER_COOKIE = "rememberedEmail";

    /** Phone number: 10 to 13 digits, optional leading +. */
    public static final Pattern PHONE = Pattern.compile("^\\+?[0-9]{10,13}$");
}
