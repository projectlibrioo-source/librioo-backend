package org.example.projectlibrioo.Service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FirebaseService {

    public void sendShelfNumber(int shelfNumber) {
        // ✅ Get FirebaseApp instance explicitly
        FirebaseApp app = FirebaseApp.getInstance();
        DatabaseReference robotRef = FirebaseDatabase.getInstance(app)
                .getReference("robot");

        Map<String, Object> updates = new HashMap<>();
        updates.put("targetShelf", shelfNumber);
        updates.put("status", "NAVIGATING");
        updates.put("currentStep", 0);
        updates.put("currentCommand", "none");

        robotRef.updateChildrenAsync(updates);
    }
}
