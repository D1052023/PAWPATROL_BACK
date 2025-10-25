package eci.edu.dosw.proyecto.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

@Profile("!test")
@Configuration
@RequiredArgsConstructor
public class JwtKeyConfig implements ResourceLoaderAware {


    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    @Value("${security.jwt.keystore.path}")
    private String keystorePath;

    @Value("${security.jwt.keystore.password}")
    private String keystorePassword;

    @Value("${security.jwt.keystore.alias}")
    private String keystoreAlias;

    @Bean
    public PrivateKey jwtPrivateKey() throws Exception {
        KeyStore ks = loadKeyStore();
        KeyStore.PasswordProtection prot = new KeyStore.PasswordProtection(keystorePassword.toCharArray());
        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(keystoreAlias, prot);
        if (pkEntry == null) {
            throw new IllegalStateException("No se encontró entrada con alias: " + keystoreAlias);
        }
        return pkEntry.getPrivateKey();
    }

    @Bean
    public PublicKey jwtPublicKey() throws Exception {
        KeyStore ks = loadKeyStore();
        Certificate cert = ks.getCertificate(keystoreAlias);
        if (cert == null) {
            throw new IllegalStateException("No se encontró certificado con alias: " + keystoreAlias);
        }
        return cert.getPublicKey();
    }

    private KeyStore loadKeyStore() throws Exception {
        try {
            Resource keystoreResource = resourceLoader.getResource(keystorePath);
            try (InputStream is = keystoreResource.getInputStream()) {
                KeyStore ks = KeyStore.getInstance("PKCS12");
                ks.load(is, keystorePassword.toCharArray());
                return ks;
            }
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo cargar el keystore [" + keystorePath + "]: " + ex.getMessage(), ex);
        }
    }
}
