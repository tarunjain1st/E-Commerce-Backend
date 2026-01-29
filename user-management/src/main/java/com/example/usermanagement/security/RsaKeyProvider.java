package com.example.usermanagement.security;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Component
public class RsaKeyProvider {

    private PrivateKey privateKey;

    @PostConstruct
    public void init() throws Exception {
        this.privateKey = loadPrivateKey();
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    private PrivateKey loadPrivateKey() throws Exception {

        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("secrets/private.pem");

        if (is == null) {
            throw new IllegalStateException("secrets/private.pem not found in classpath");
        }

        String key = new String(is.readAllBytes(), StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(key);

        return KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }
}
