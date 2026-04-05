package org.example.projectlibrioo.Service.RobotService;

import org.example.projectlibrioo.Model.Robot;
import org.example.projectlibrioo.Model.RobotMaintenance;
import org.example.projectlibrioo.Repository.RobotMaintenanceRepo;
import org.example.projectlibrioo.Repository.RobotRepo;

import org.example.projectlibrioo.navigation.ShelfPathMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class RobotService {

    private static final Logger log = LoggerFactory.getLogger(RobotService.class);

    private final ShelfPathMap shelfPathMap;
    private final RobotRepo robotRepo;
    private final RobotMaintenanceRepo robotMaintenanceRepo;
      // ← single source of truth for Firebase

    public RobotService(ShelfPathMap shelfPathMap,
                        RobotRepo robotRepo,
                        RobotMaintenanceRepo robotMaintenanceRepo) {
        this.shelfPathMap      = shelfPathMap;
        this.robotRepo         = robotRepo;
        this.robotMaintenanceRepo = robotMaintenanceRepo;
        
    }

    // ══════════════════════════════════════════════════════════════
    //  FIREBASE COMMANDS  — all delegated to FirebaseService
    // ══════════════════════════════════════════════════════════════
    private DatabaseReference getRobotRef() {
    FirebaseApp app = FirebaseApp.getInstance();
    return FirebaseDatabase.getInstance(app).getReference("robot");
}
    public void navigateToShelf(int shelfNumber) {
        try {
            FirebaseApp app = FirebaseApp.getInstance();

            DatabaseReference rootRef = FirebaseDatabase.getInstance(app).getReference();
            DatabaseReference pathRef = rootRef.child("paths").child(String.format("%02d", shelfNumber));
            DatabaseReference robotRef = rootRef.child("robot");

            // 🔥 Read path from Firebase
            final List<String> pathList = new ArrayList<>();
            final CountDownLatch latch = new CountDownLatch(1);

            pathRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            pathList.add(child.getValue(String.class));
                        }
                    }
                    latch.countDown();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    System.out.println("Error: " + error.getMessage());
                    latch.countDown();
                }
            });

            latch.await(5, TimeUnit.SECONDS);

            // 🚨 CHECK IF PATH FOUND
            if (pathList.isEmpty()) {
                System.out.println("No path found for shelf " + shelfNumber);
                return;
            }

            // 🔥 SEND TO ROBOT NODE
            Map<String, Object> updates = new HashMap<>();
            updates.put("targetShelf", shelfNumber);
            updates.put("path", pathList);
            updates.put("currentCommand", "START");
            updates.put("currentStep", 0);
            updates.put("status", "MOVING");

            robotRef.updateChildrenAsync(updates);

            System.out.println("Sent path to robot: " + pathList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendBackCommand() {
    try {
        DatabaseReference ref = getRobotRef();

        Map<String, Object> updates = new HashMap<>();
        updates.put("currentCommand", "BACK");
        updates.put("status", "RETURNING");

        ref.updateChildrenAsync(updates);

        log.info("BACK command sent");

    } catch (Exception e) {
        log.error("Failed to send BACK: {}", e.getMessage());
    }
}
    public void sendStopCommand() {
    try {
        DatabaseReference ref = getRobotRef();

        Map<String, Object> updates = new HashMap<>();
        updates.put("currentCommand", "STOP");
        updates.put("status", "STOPPED");

        ref.updateChildrenAsync(updates);

        log.info("STOP command sent");

    } catch (Exception e) {
        log.error("Failed to send STOP: {}", e.getMessage());
    }
}

    public String getRobotStatus() {
    try {
        DatabaseReference ref = getRobotRef().child("status");

        final String[] status = {"UNKNOWN"};
        CountDownLatch latch = new CountDownLatch(1);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    status[0] = snapshot.getValue(String.class);
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                log.error("Firebase read cancelled: {}", error.getMessage());
                latch.countDown();
            }
        });

        latch.await(5, TimeUnit.SECONDS);
        return status[0];

    } catch (Exception e) {
        log.error("getRobotStatus failed: {}", e.getMessage());
        return "ERROR";
    }
}

    // ══════════════════════════════════════════════════════════════
    //  ROBOT CRUD
    // ══════════════════════════════════════════════════════════════

    public Robot saveRobot(Robot robot) {
        if (robot.getStatus() == null)    robot.setStatus("ACTIVE");
        if (robot.getStartDate() == null) robot.setStartDate(LocalDate.now());
        return robotRepo.save(robot);
    }

    public List<Robot> getAllRobots() {
        return robotRepo.findAll();
    }

    public Robot getRobotById(int robotId) {
        return robotRepo.findById(robotId).orElse(null);
    }

    public Robot getRobotByName(String robotName) {
        return robotRepo.findByRobotName(robotName).orElse(null);
    }

    public Robot updateRobot(Robot robot) {
        if (!robotRepo.existsById(robot.getRobotID())) return null;
        return robotRepo.save(robot);
    }

    public boolean deleteRobot(int robotId) {
        if (!robotRepo.existsById(robotId)) return false;
        robotRepo.deleteById(robotId);
        return true;
    }

    public boolean existsByRobotName(String robotName) {
        return robotRepo.existsByRobotName(robotName);
    }

    // ══════════════════════════════════════════════════════════════
    //  MAINTENANCE
    // ══════════════════════════════════════════════════════════════

    public RobotMaintenance logMaintenance(int robotId,
                                           LocalDate lastServiceDate,
                                           LocalDate nextServiceDate,
                                           String partReplaced,
                                           String technicianNotes) {
        Robot robot = getRobotById(robotId);
        if (robot == null) return null;

        RobotMaintenance entry = new RobotMaintenance();
        entry.setRobot(robot);
        entry.setLastServiceDate(lastServiceDate);
        entry.setNextServiceDate(nextServiceDate);
        entry.setPartReplaced(partReplaced);
        entry.setTechnicianNotes(technicianNotes);

        robot.setStatus("MAINTENANCE");
        robotRepo.save(robot);

        // Stop the physical robot while under maintenance
        sendStopCommand();
                

        return robotMaintenanceRepo.save(entry);
    }

    public List<RobotMaintenance> getMaintenanceHistory(int robotId) {
        return robotMaintenanceRepo.findByRobot_RobotIDOrderByLoggedAtDesc(robotId);
    }
}