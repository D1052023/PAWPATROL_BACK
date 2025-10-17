package eci.edu.dosw.proyecto.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class StatusController {

    private static final Logger logger = LoggerFactory.getLogger(StatusController.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/status")
    public ResponseEntity<String> checkStatus() {
        if (mongoTemplate == null) {
            logger.error("MongoTemplate no está inicializado!");
            return ResponseEntity.status(500).body("❌ MongoTemplate is not initialized!");
        }

        try {
            mongoTemplate.getDb().listCollectionNames().first();
            logger.info("Conexión a MongoDB (Cosmos) exitosa!");
            return ResponseEntity.ok("✅ Connected to MongoDB (Cosmos) successfully!");
        } catch (Exception e) {
            logger.error("Error al conectar con MongoDB: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body("❌ Connection error: " + e.getMessage());
        }
    }
}