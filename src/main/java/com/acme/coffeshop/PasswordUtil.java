package com.acme.coffeshop;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

final class PasswordUtil {
    private static final int ITERATIONS = 210_000;
    private static final int SALT_BYTES = 16;
    private static final int HASH_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    static Credential create(String password) throws GeneralSecurityException {
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        return new Credential(derive(password, salt), salt);
    }

    static boolean verify(String password, byte[] expectedHash, byte[] salt) throws GeneralSecurityException {
        return MessageDigest.isEqual(derive(password, salt), expectedHash);
    }

    private static byte[] derive(String password, byte[] salt) throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, HASH_BITS);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword();
        }
    }

    static final class Credential {
        private final byte[] hash;
        private final byte[] salt;

        Credential(byte[] hash, byte[] salt) {
            this.hash = hash.clone();
            this.salt = salt.clone();
        }

        byte[] getHash() {
            return hash.clone();
        }

        byte[] getSalt() {
            return salt.clone();
        }
    }
}
