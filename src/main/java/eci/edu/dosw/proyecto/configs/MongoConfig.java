package eci.edu.dosw.proyecto.configs;

import org.bson.UuidRepresentation;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import com.mongodb.MongoClientSettings;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "sirha_preprod";
    }

    @Override
    protected MongoClientSettings mongoClientSettings() {
        return MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build();
    }
}
