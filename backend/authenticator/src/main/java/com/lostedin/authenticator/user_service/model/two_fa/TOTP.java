package com.lostedin.authenticator.user_service.model.two_fa;

import org.springframework.stereotype.Service;

@Service
public class TOTP {
    // RFC 6238 (TOTP) implementation with RFC 4226 (HOTP) truncation and RFC 4648 Base32

    // Default parameters
    private static final int DEFAULT_DIGITS = 6;
    private static final int DEFAULT_PERIOD = 30; // seconds
    private static final String DEFAULT_ALGO = "HmacSHA1"; // also supports HmacSHA256, HmacSHA512

    /**
     * Generate a random Base32 (RFC 4648) secret suitable for TOTP.
     * @param numBytes number of random bytes before Base32 encoding (recommend 20)
     */
    public String generateSecret(int numBytes) {
        if (numBytes <= 0) throw new IllegalArgumentException("numBytes must be > 0");
        byte[] random = new byte[numBytes];
        java.security.SecureRandom rnd = new java.security.SecureRandom();
        rnd.nextBytes(random);
        return Base32.encode(random);
    }

    /**
     * Generate current TOTP code for the given Base32 secret using default parameters.
     */
    public String currentCode(String base32Secret) {
        return codeAt(base32Secret, System.currentTimeMillis() / 1000L, DEFAULT_DIGITS, DEFAULT_PERIOD, DEFAULT_ALGO);
    }

    /**
     * Verify a code allowing a small window of time-steps to account for clock skew.
     * @param window number of steps to check before/after current (e.g., 1 allows +/- 1 step)
     */
    public boolean verifyCode(String base32Secret, String code, int window) {
        return verifyCode(base32Secret, code, DEFAULT_DIGITS, DEFAULT_PERIOD, DEFAULT_ALGO, window);
    }

    public boolean verifyCode(String base32Secret, String code, int digits, int period, String algorithm, int window) {
        if (code == null) return false;
        long now = System.currentTimeMillis() / 1000L;
        for (int i = -window; i <= window; i++) {
            String candidate = codeAt(base32Secret, now + (long) i * period, digits, period, algorithm);
            if (constantTimeEquals(code, candidate)) return true;
        }
        return false;
    }

    /**
     * Compute TOTP code at a specific Unix time (seconds).
     */
    public String codeAt(String base32Secret, long timeSeconds, int digits, int period, String algorithm) {
        if (digits < 6 || digits > 10) throw new IllegalArgumentException("digits must be between 6 and 10");
        if (period <= 0) throw new IllegalArgumentException("period must be > 0");
        byte[] key = Base32.decode(base32Secret);
        long counter = timeSeconds / period;
        try {
            byte[] counterBytes = new byte[8];
            for (int i = 7; i >= 0; i--) { // big-endian
                counterBytes[i] = (byte) (counter & 0xFF);
                counter >>= 8;
            }
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance(algorithm);
            mac.init(new javax.crypto.spec.SecretKeySpec(key, algorithm));
            byte[] hash = mac.doFinal(counterBytes);

            int offset = hash[hash.length - 1] & 0x0F;
            int binary = ((hash[offset] & 0x7F) << 24)
                    | ((hash[offset + 1] & 0xFF) << 16)
                    | ((hash[offset + 2] & 0xFF) << 8)
                    | (hash[offset + 3] & 0xFF);

            int mod = (int) Math.pow(10, digits);
            int otp = binary % mod;
            return leftPad(Integer.toString(otp), digits, '0');
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate TOTP: " + e.getMessage(), e);
        }
    }

    public String buildOtpAuthUri(String issuer, String accountName, String base32Secret) {
        return buildOtpAuthUri(issuer, accountName, base32Secret, DEFAULT_DIGITS, DEFAULT_PERIOD, DEFAULT_ALGO);
    }

    public String buildOtpAuthUri(String issuer, String accountName, String base32Secret, int digits, int period, String algorithm) {
        if (issuer == null) issuer = "";
        if (accountName == null) accountName = "";
        String label = urlEncode(issuer.isEmpty() ? accountName : issuer + ":" + accountName);
        StringBuilder sb = new StringBuilder("otpauth://totp/")
                .append(label)
                .append("?secret=").append(base32Secret)
                .append("&issuer=").append(urlEncode(issuer))
                .append("&period=").append(period)
                .append("&digits=").append(digits)
                .append("&algorithm=").append(algorithm.replace("Hmac", ""));
        return sb.toString();
    }

    private static String leftPad(String s, int len, char ch) {
        if (s.length() >= len) return s;
        StringBuilder sb = new StringBuilder(len);
        for (int i = s.length(); i < len; i++) sb.append(ch);
        sb.append(s);
        return sb.toString();
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        int len = Math.max(a.length(), b.length());
        int diff = 0;
        for (int i = 0; i < len; i++) {
            char ca = i < a.length() ? a.charAt(i) : 0;
            char cb = i < b.length() ? b.charAt(i) : 0;
            diff |= ca ^ cb;
        }
        return diff == 0 && a.length() == b.length();
    }

    private static String urlEncode(String s) {
        try {
            return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            return s;
        }
    }

    // Minimal RFC 4648 Base32 without padding management complexities for common TOTP secrets.
    private static final class Base32 {
        private static final char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray();
        private static final int[] LOOKUP = new int[128];
        static {
            java.util.Arrays.fill(LOOKUP, -1);
            for (int i = 0; i < ALPHABET.length; i++) LOOKUP[ALPHABET[i]] = i;
            // also accept lowercase
            for (int i = 0; i < ALPHABET.length; i++) LOOKUP[Character.toLowerCase(ALPHABET[i])] = i;
            LOOKUP['='] = 0; // padding
        }

        static String encode(byte[] data) {
            if (data == null || data.length == 0) return "";
            StringBuilder sb = new StringBuilder((data.length * 8 + 4) / 5);
            int buffer = 0;
            int bitsLeft = 0;
            for (byte b : data) {
                buffer = (buffer << 8) | (b & 0xFF);
                bitsLeft += 8;
                while (bitsLeft >= 5) {
                    int index = (buffer >> (bitsLeft - 5)) & 0x1F;
                    bitsLeft -= 5;
                    sb.append(ALPHABET[index]);
                }
            }
            if (bitsLeft > 0) {
                int index = (buffer << (5 - bitsLeft)) & 0x1F;
                sb.append(ALPHABET[index]);
            }
            // Standard Base32 uses padding '=', but authenticator apps accept unpadded too.
            return sb.toString();
        }

        static byte[] decode(String s) {
            if (s == null || s.isEmpty()) return new byte[0];
            int buffer = 0;
            int bitsLeft = 0;
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream(s.length() * 5 / 8);
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c >= LOOKUP.length) continue;
                int val = LOOKUP[c];
                if (val < 0) continue; // skip invalid chars (spaces, dashes)
                buffer = (buffer << 5) | val;
                bitsLeft += 5;
                if (bitsLeft >= 8) {
                    out.write((buffer >> (bitsLeft - 8)) & 0xFF);
                    bitsLeft -= 8;
                }
            }
            return out.toByteArray();
        }
    }
}
