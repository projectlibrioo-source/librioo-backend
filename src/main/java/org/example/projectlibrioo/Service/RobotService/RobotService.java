package org.example.projectlibrioo.Service.RobotService;

import org.example.projectlibrioo.Model.Robot;
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

    public RobotService(ShelfPathMap shelfPathMap, RobotRepo robotRepo) {
        this.shelfPathMap = shelfPathMap;
        this.robotRepo = robotRepo;
    }

    /*public void navigateToShelf(int shelfNumber) {

        List<String> path = shelfPathMap.getPath(shelfNumber);
        String command = String.join(",", path);

        String url = "http://192.168.1.5/move?cmd=" + command;
        restTemplate.getForObject(url, String.class);
    }*/



    public void navigateToShelf(int shelfNumber) {

        List<String> path = shelfPathMap.getPath(shelfNumber);
        String command = String.join(",", path);

        String encodedCommand = URLEncoder.encode(command, StandardCharsets.UTF_8);

        String url = "http://10.102.165.232/send?num=" + encodedCommand;
        restTemplate.getForObject(url, String.class);
    }

// ---------- NEW: CRUD methods for Robot entity ----------

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

    // Update robot maintenance
    public Robot updateRobotMaintenance(int robotId,
                                        LocalDate lastServiceDate,
                                        LocalDate nextServiceDate,
                                        String partReplaced,
                                        String technicianNotes) {
        Robot robot = getRobotById(robotId);
        if (robot != null) {
            robot.setLastServiceDate(lastServiceDate);
            robot.setNextServiceDate(nextServiceDate);
            robot.setPartReplaced(partReplaced);
            robot.setTechnicianNotes(technicianNotes);
            robot.setStatus("MAINTENANCE");
            return robotRepo.save(robot);
        }
        return null;
    }

}
