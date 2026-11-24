package com.infy.scm360.common.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class RsaKeyUtils {

    private static final Logger log = LoggerFactory.getLogger(RsaKeyUtils.class);

    // Deterministic dev fallback key pair so all services can decode dev tokens without setup
    private static final KeyPair DEV_KEY_PAIR;

    static {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048, new SecureRandom(new byte[]{42, 13, 37, 99}));
            DEV_KEY_PAIR = generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private RsaKeyUtils() {}

    public static PublicKey getDevPublicKey() {
        return DEV_KEY_PAIR.getPublic();
    }

    public static PrivateKey getDevPrivateKey() {
        return DEV_KEY_PAIR.getPrivate();
    }

    public static PublicKey parsePublicKey(String pem) {
        if (pem == null || pem.isBlank()) {
            return getDevPublicKey();
        }
        try {
            String clean = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                              .replace("-----END PUBLIC KEY-----", "")
                              .replaceAll("\\s+", "");
            byte[] decoded = Base64.getDecoder().decode(clean);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(new X509EncodedKeySpec(decoded));
        } catch (Exception e) {
            log.warn("Failed to parse provided public key PEM, falling back to development RSA key: {}", e.getMessage());
            return getDevPublicKey();
        }
    }

    public static PrivateKey parsePrivateKey(String pem) {
        if (pem == null || pem.isBlank()) {
            return getDevPrivateKey();
        }
        try {
            String clean = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                              .replace("-----END PRIVATE KEY-----", "")
                              .replaceAll("\\s+", "");
            byte[] decoded = Base64.getDecoder().decode(clean);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(new PKCS8EncodedKeySpec(decoded));
        } catch (Exception e) {
            log.warn("Failed to parse provided private key PEM, falling back to development RSA key: {}", e.getMessage());
            return getDevPrivateKey();
        }
    }
}
