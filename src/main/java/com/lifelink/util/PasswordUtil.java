package com.lifelink.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Safe password storage: random salt + PBKDF2 hashing (never store plain passwords). */
public final class PasswordUtil {
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;

    private PasswordUtil() { }

    public static String newSalt() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    public static String hash(String password, String saltHex) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(),
                    HexFormat.of().parseHex(saltHex), ITERATIONS, KEY_LENGTH);
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Password hashing failed", e);
        }
    }

    public static boolean matches(String password, String saltHex, String expectedHash) {
        String actual = hash(password, saltHex);
        return MessageDigest.isEqual(actual.getBytes(), expectedHash.getBytes());
    }
}
