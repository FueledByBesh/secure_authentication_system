package com.lostedin.authenticator.user_service.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Objects;

/**
 * Utility for hashing and verifying passwords using PBKDF2 with HMAC-SHA256.
 *
 * Stored format: pbkdf2$<iterations>$<saltBase64>$<hashBase64>
 */
public class PasswordEncrypter {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int SALT_LENGTH_BYTES = 16; // 128-bit salt
    private static final int HASH_LENGTH_BITS = 256; // 256-bit hash
    private static final int DEFAULT_ITERATIONS = 210_000; // OWASP recommended order of magnitude

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordEncrypter() {}

    /**
     * Hashes a raw password using PBKDF2WithHmacSHA256 and returns a formatted string safe to store.
     */
    public static String hash(String rawPassword) {
        Objects.requireNonNull(rawPassword, "rawPassword");
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        SECURE_RANDOM.nextBytes(salt);
        byte[] derived = pbkdf2(rawPassword.toCharArray(), salt, DEFAULT_ITERATIONS, HASH_LENGTH_BITS);
        String saltB64 = Base64.getEncoder().encodeToString(salt);
        String hashB64 = Base64.getEncoder().encodeToString(derived);
        return String.format("pbkdf2$%d$%s$%s", DEFAULT_ITERATIONS, saltB64, hashB64);
    }

    /**
     * Verifies a raw password against a previously stored hash string.
     */
    public static boolean verify(String rawPassword, String stored) {
        if (rawPassword == null || stored == null) return false;
        String[] parts = stored.split("\\$");
        if (parts.length != 4 || !"pbkdf2".equals(parts[0])) return false;
        int iterations;
        try {
            iterations = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return false;
        }
        byte[] salt;
        byte[] expected;
        try {
            salt = Base64.getDecoder().decode(parts[2]);
            expected = Base64.getDecoder().decode(parts[3]);
        } catch (IllegalArgumentException e) {
            return false;
        }
        byte[] actual = pbkdf2(rawPassword.toCharArray(), salt, iterations, expected.length * 8);
        return constantTimeEquals(expected, actual);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLengthBits) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLengthBits);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Password hashing failure", e);
        } finally {
            spec.clearPassword();
        }
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a == null || b == null) return false;
        if (a.length != b.length) return false;
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= (a[i] ^ b[i]);
        }
        return result == 0;
    }
}
