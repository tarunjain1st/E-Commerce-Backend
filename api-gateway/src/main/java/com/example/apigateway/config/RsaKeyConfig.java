package com.example.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class RsaKeyConfig {
    @Bean
    public PublicKey publicKey() throws Exception {
//        InputStream is = getClass()
//                .getClassLoader()
//                .getResourceAsStream("secrets/public.pem");
//
//        if (is == null) {
//            throw new IllegalStateException("secrets/public.pem not found in classpath");
//        }
//        String key = new String(is.readAllBytes(), StandardCharsets.UTF_8)
//                .replace("-----BEGIN PUBLIC KEY-----", "")
//                .replace("-----END PUBLIC KEY-----", "")
//                .replaceAll("\\s+", "");

        ClassPathResource publicKeyResource = new ClassPathResource("secrets/public.pem");
        String key = new String(Files.readAllBytes(publicKeyResource.getFile().toPath()))
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}
