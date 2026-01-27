package id.co.lua.pbj.penilaian_karyawan.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class MyStringFormat {

    private MyStringFormat() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // Define a URL-safe Base64 encoder
    private static final Base64.Encoder URL_SAFE_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final SecureRandom RANDOM = new SecureRandom(); // Secure random number generator

    /**
     * Generates a URL-safe token of the specified length.
     *
     * @param length The length of the token to generate.
     * @return A URL-safe token of the specified length.
     */
    public static String generateToken(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }
        
        // Calculate the number of bytes required to get the desired token length
        int byteLength = (int) Math.ceil(length * 3.0 / 4.0);
        byte[] randomBytes = new byte[byteLength];
        RANDOM.nextBytes(randomBytes);
        
        // Encode bytes to URL-safe Base64 and trim to the desired length
        String token = URL_SAFE_ENCODER.encodeToString(randomBytes);
        return token.substring(0, Math.min(length, token.length()));
    }
}