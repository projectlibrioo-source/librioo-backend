package org.example.projectlibrioo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {


    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {

                String firebaseJson = System.getenv("FIREBASE_CONFIG");

                if (firebaseJson == null || firebaseJson.isEmpty()) {
                    throw new RuntimeException("❌ Firebase ENV not set");
                }

                ByteArrayInputStream serviceAccount =
                        new ByteArrayInputStream(firebaseJson.getBytes(StandardCharsets.UTF_8));

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setDatabaseUrl("https://librioo-fb90e-default-rtdb.firebaseio.com")
                        .build();

                FirebaseApp.initializeApp(options);

                System.out.println("🔥 Firebase initialized successfully (Railway)");

            }
        } catch (Exception e) {
            System.out.println("❌ Firebase initialization failed");
            e.printStackTrace();
        }
    }
}
