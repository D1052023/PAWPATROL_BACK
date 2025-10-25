package eci.edu.dosw.proyecto.configs;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

@TestConfiguration
public class TestJwtKeyConfig {

    @Bean
    public PrivateKey jwtPrivateKey() throws Exception {
        return keyPair().getPrivate();
    }

    @Bean
    public PublicKey jwtPublicKey() throws Exception {
        return keyPair().getPublic();
    }

    private KeyPair keyPair() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        return kpg.generateKeyPair();
    }
}
