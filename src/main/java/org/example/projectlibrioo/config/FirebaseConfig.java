package org.example.projectlibrioo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {

                // 🔐 Read Base64 from Railway ENV
                String base64 = System.getenv("FIREBASE_CONFIG_BASE64");

                if (base64 == null || base64.isEmpty()) {
                    throw new RuntimeException("❌ FIREBASE_CONFIG_BASE64 not set");
                }

                // 🔥 Decode Base64 → original JSON
                byte[] decoded = Base64.getDecoder().decode(base64);

                InputStream serviceAccount =
                        new ByteArrayInputStream(decoded);

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setDatabaseUrl("https://librioo-fb90e-default-rtdb.firebaseio.com/")
                        .build();

                FirebaseApp.initializeApp(options);


                System.out.println("🔥 Firebase initialized successfully (Base64)");

                FirebaseApp app = FirebaseApp.getInstance();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("test");

                ref.setValueAsync("hello")
                .addListener(() -> System.out.println("✅ WRITE SUCCESS"), Runnable::run);
                System.out.println(app.getName());

            }
        } catch (Exception e) {
            System.out.println("❌ Firebase initialization failed");
            e.printStackTrace();
        }
    }
}
