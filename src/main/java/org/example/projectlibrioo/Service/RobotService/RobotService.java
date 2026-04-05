package org.example.projectlibrioo.Service.RobotService;

import org.example.projectlibrioo.Model.Robot;
import org.example.projectlibrioo.Model.RobotMaintenance;
import org.example.projectlibrioo.Repository.RobotMaintenanceRepo;
import org.example.projectlibrioo.Repository.RobotRepo;
import org.example.projectlibrioo.Service.FirebaseService;
import org.example.projectlibrioo.navigation.ShelfPathMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RobotService {

    private static final Logger log = LoggerFactory.getLogger(RobotService.class);

    private final ShelfPathMap shelfPathMap;
    private final RobotRepo robotRepo;
    private final RobotMaintenanceRepo robotMaintenanceRepo;
    private final FirebaseService firebaseService;  // ← single source of truth for Firebase

    public RobotService(ShelfPathMap shelfPathMap,
                        RobotRepo robotRepo,
                        RobotMaintenanceRepo robotMaintenanceRepo,
                        FirebaseService firebaseService) {
        this.shelfPathMap      = shelfPathMap;
        this.robotRepo         = robotRepo;
        this.robotMaintenanceRepo = robotMaintenanceRepo;
        this.firebaseService   = firebaseService;
    }

    // ══════════════════════════════════════════════════════════════
    //  FIREBASE COMMANDS  — all delegated to FirebaseService
    // ══════════════════════════════════════════════════════════════

    public void navigateToShelf(int shelfNumber) {
        firebaseService.sendShelfNumber(shelfNumber)
                .thenRun(() -> log.info("Shelf {} dispatched to Firebase", shelfNumber))
                .exceptionally(ex -> {
                    log.error("Failed to send shelf {} to Firebase: {}", shelfNumber, ex.getMessage());
                    return null;
                });
    }

    public void sendBackCommand() {
        firebaseService.sendBackCommand()
                .thenRun(() -> log.info("BACK command dispatched"))
                .exceptionally(ex -> {
                    log.error("Failed to send BACK: {}", ex.getMessage());
                    return null;
                });
    }

    public void sendStopCommand() {
        firebaseService.sendStopCommand()
                .thenRun(() -> log.info("STOP command dispatched"))
                .exceptionally(ex -> {
                    log.error("Failed to send STOP: {}", ex.getMessage());
                    return null;
                });
    }

    public String getRobotStatus() {
        try {
            FirebaseService.RobotState state = firebaseService.getRobotState().get(5, java.util.concurrent.TimeUnit.SECONDS);
            return state.status();
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
        firebaseService.sendStopCommand()
                .exceptionally(ex -> {
                    log.warn("Could not stop robot during maintenance log: {}", ex.getMessage());
                    return null;
                });

        return robotMaintenanceRepo.save(entry);
    }

    public List<RobotMaintenance> getMaintenanceHistory(int robotId) {
        return robotMaintenanceRepo.findByRobot_RobotIDOrderByLoggedAtDesc(robotId);
    }
}