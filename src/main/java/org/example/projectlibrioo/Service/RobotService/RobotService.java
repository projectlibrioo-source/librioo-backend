package org.example.projectlibrioo.Service.RobotService;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.example.projectlibrioo.Model.Robot;
import org.example.projectlibrioo.Model.RobotMaintenance;
import org.example.projectlibrioo.Repository.RobotMaintenanceRepo;
import org.example.projectlibrioo.Repository.RobotRepo;
import org.example.projectlibrioo.navigation.ShelfPathMap;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RobotService {
    private final ShelfPathMap shelfPathMap;
    private final RestTemplate restTemplate = new RestTemplate();
    private final RobotRepo robotRepo;
    private final RobotMaintenanceRepo robotMaintenanceRepo;

    public RobotService(ShelfPathMap shelfPathMap, RobotRepo robotRepo, RobotMaintenanceRepo robotMaintenanceRepo) {
        this.shelfPathMap = shelfPathMap;
        this.robotRepo = robotRepo;
        this.robotMaintenanceRepo = robotMaintenanceRepo;
    }

    // ===== NAVIGATE TO SHELF =====
    public void navigateToShelf(int shelfNumber) {
        try {
            FirebaseApp app = FirebaseApp.getInstance();
            DatabaseReference ref = FirebaseDatabase.getInstance(app).getReference("robot");

            ref.child("targetShelf").setValueAsync(shelfNumber);
            ref.child("status").setValueAsync("MOVING");
            ref.child("currentCommand").setValueAsync("none"); // FIX: "none" not ""
            ref.child("currentStep").setValueAsync(0);         // reset step progress

            System.out.println("Sent shelf " + shelfNumber + " to Firebase");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== SEND BACK COMMAND =====
    public void sendBackCommand() {
        try {
            FirebaseApp app = FirebaseApp.getInstance();
            DatabaseReference ref = FirebaseDatabase.getInstance(app).getReference("robot");

            ref.child("currentCommand").setValueAsync("BACK");
            ref.child("status").setValueAsync("RETURNING");

            System.out.println("Sent BACK to Firebase");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== SEND STOP COMMAND =====
    public void sendStopCommand() {
        try {
            FirebaseApp app = FirebaseApp.getInstance();
            DatabaseReference ref = FirebaseDatabase.getInstance(app).getReference("robot");

            ref.child("currentCommand").setValueAsync("STOP");
            ref.child("status").setValueAsync("STOPPED");

            System.out.println("Sent STOP to Firebase");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== GET CURRENT ROBOT STATUS FROM FIREBASE =====
    public String getRobotStatus() {
        try {
            FirebaseApp app = FirebaseApp.getInstance();
            DatabaseReference ref = FirebaseDatabase.getInstance(app)
                    .getReference("robot/status");

            // Use a semaphore to wait for async Firebase read
            final String[] status = {"UNKNOWN"};
            final java.util.concurrent.CountDownLatch latch =
                    new java.util.concurrent.CountDownLatch(1);

            ref.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        status[0] = snapshot.getValue(String.class);
                    }
                    latch.countDown();
                }

                @Override
                public void onCancelled(com.google.firebase.database.DatabaseError error) {
                    System.out.println("Firebase read cancelled: " + error.getMessage());
                    latch.countDown();
                }
            });

            latch.await(5, java.util.concurrent.TimeUnit.SECONDS); // wait max 5s
            return status[0];

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    // ---------- ROBOT CRUD ----------

    // Add new robot
    public Robot saveRobot(Robot robot) {
        if (robot.getStatus() == null) {
            robot.setStatus("ACTIVE");
        }
        if (robot.getStartDate() == null) {
            robot.setStartDate(LocalDate.now());
        }
        return robotRepo.save(robot);
    }

    // Get all robots
    public List<Robot> getAllRobots() {
        return robotRepo.findAll();
    }

    // Get robot by ID
    public Robot getRobotById(int robotId) {
        Optional<Robot> robot = robotRepo.findById(robotId);
        return robot.orElse(null);
    }

    // Get robot by name
    public Robot getRobotByName(String robotName) {
        Optional<Robot> robot = robotRepo.findByRobotName(robotName);
        return robot.orElse(null);
    }

    // Update robot details
    public Robot updateRobot(Robot robot) {
        if (robotRepo.existsById(robot.getRobotID())) {
            return robotRepo.save(robot);
        }
        return null;
    }

    // Delete robot
    public boolean deleteRobot(int robotId) {
        if (robotRepo.existsById(robotId)) {
            robotRepo.deleteById(robotId);
            return true;
        }
        return false;
    }

    // Check if robot exists by name
    public boolean existsByRobotName(String robotName) {
        return robotRepo.existsByRobotName(robotName);
    }

    // ---------- MAINTENANCE (separate table) ----------

    // Log a new maintenance entry into robot_maintenance table
    public RobotMaintenance logMaintenance(int robotId,
                                           LocalDate lastServiceDate,
                                           LocalDate nextServiceDate,
                                           String partReplaced,
                                           String technicianNotes) {
        Robot robot = getRobotById(robotId);
        if (robot == null) return null;

        RobotMaintenance log = new RobotMaintenance();
        log.setRobot(robot);
        log.setLastServiceDate(lastServiceDate);
        log.setNextServiceDate(nextServiceDate);
        log.setPartReplaced(partReplaced);
        log.setTechnicianNotes(technicianNotes);
        // loggedAt is auto-set via @PrePersist in RobotMaintenance

        // Also flip robot status to MAINTENANCE
        robot.setStatus("MAINTENANCE");
        robotRepo.save(robot);

        return robotMaintenanceRepo.save(log);
    }

    // Get full maintenance history for a robot (newest first)
    public List<RobotMaintenance> getMaintenanceHistory(int robotId) {
        return robotMaintenanceRepo.findByRobot_RobotIDOrderByLoggedAtDesc(robotId);
    }
}
