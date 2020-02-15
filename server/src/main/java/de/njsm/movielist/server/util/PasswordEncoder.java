package de.njsm.movielist.server.util;

import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

public class PasswordEncoder {

    private int iterations;

    private String salt;

    private byte[] password;

    public PasswordEncoder(int iterations, String salt, byte[] password) {
        this.iterations = iterations;
        this.salt = salt;
        this.password = password;
    }

    public boolean matches(Object credentials) {
        if (credentials instanceof String) {
            String challenge = (String) credentials;
            byte[] encoded = encode(challenge, salt.getBytes());
            return MessageDigest.isEqual(password, encoded);
        } else {
            return false;
        }
    }

    private byte[] encode(String rawPassword, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), salt, this.iterations, 256);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256.name());
            return skf.generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not create hash", e);
        }
    }
}