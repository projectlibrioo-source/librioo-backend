package org.example.projectlibrioo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
public class FirebaseConfig {


    @PostConstruct
    public void initialize() throws Exception {
        System.out.println("FirebaseConfig loaded");
        try {
            if (FirebaseApp.getApps().isEmpty()) {

                InputStream serviceAccount = getClass()
                        .getClassLoader()
                        .getResourceAsStream("librioo-fb90e-firebase-adminsdk-fbsvc-430550eddd.json");

                if (serviceAccount == null) {
                    throw new RuntimeException("❌ Firebase JSON file NOT FOUND");
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setDatabaseUrl("https://librioo-fb90e-default-rtdb.firebaseio.com")
                        .build();

                FirebaseApp.initializeApp(options);

                System.out.println("🔥 Firebase initialized successfully");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
