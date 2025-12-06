package com.lostedin.authenticator.auth_service.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Simple BCrypt hasher utility.
 */
public class Hasher {

    private Hasher() {}

    /**
     * Hash a plaintext using BCrypt with a reasonable work factor.
     */
    public static String bcrypt(String plaintext) throws IllegalArgumentException {
        if (plaintext == null) throw new IllegalArgumentException("plaintext cannot be null");
        String salt = BCrypt.gensalt(12); // cost 12 is a good default
        return BCrypt.hashpw(plaintext, salt);
    }

    /**
     * Verify plaintext against a BCrypt hash.
     */
    public static boolean verify(String plaintext, String hash) {
        if (plaintext == null || hash == null) return false;
        try {
            return BCrypt.checkpw(plaintext, hash);
        } catch (IllegalArgumentException e) {
            // invalid hash format
            return false;
        }
    }
}
