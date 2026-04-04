package org.example.projectlibrioo.Controller.Robot;

import org.example.projectlibrioo.Model.Robot;
import org.example.projectlibrioo.Model.RobotMaintenance;
import org.example.projectlibrioo.Repository.RobotRepo;
import org.example.projectlibrioo.Service.RobotService.RobotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RobotController {

    @Autowired
    private RobotService robotService;
    @Autowired
    private RobotRepo robotRepository;

    // Navigate to shelf (POST /api/robot/navigate?shelf=3)
    @PostMapping("/robot/navigate")
    public ResponseEntity<String> navigateToShelf(@RequestParam("shelf") int shelf) {
        try {
            robotService.navigateToShelf(shelf);
            return ResponseEntity.ok("Moving to shelf " + shelf);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR: " + e.getMessage());
        }
    }

    // Send BACK command (POST /api/robot/back)
    @PostMapping("/robot/back")
    public ResponseEntity<String> sendBack() {
        try {
            robotService.sendBackCommand();
            return ResponseEntity.ok("Robot returning to start");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR: " + e.getMessage());
        }
    }

    // Send STOP command (POST /api/robot/stop)
    @PostMapping("/robot/stop")
    public ResponseEntity<String> sendStop() {
        try {
            robotService.sendStopCommand();
            return ResponseEntity.ok("Robot stopped");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR: " + e.getMessage());
        }
    }

    // Get current robot status from Firebase (GET /api/robot/status)
    @GetMapping("/robot/status")
    public ResponseEntity<String> getRobotStatus() {
        try {
            String status = robotService.getRobotStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR: " + e.getMessage());
        }
    }

    // Add new robot (POST /api/robots/add)
    @PostMapping("/robots/add")
    public ResponseEntity<?> addRobot(@RequestBody Robot robot) {
        try {
            // Check if robot with same name exists
            if (robotService.existsByRobotName(robot.getRobotName())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Robot with this name already exists");
            }

            Robot savedRobot = robotService.saveRobot(robot);
            if (savedRobot != null) {
                return new ResponseEntity<>(savedRobot, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR: " + e.getMessage());
        }
    }

    // Get all robots (GET /api/robots/all)
    @GetMapping("/robots/all")
    public ResponseEntity<List<Robot>> getAllRobots() {
        List<Robot> robots = robotService.getAllRobots();
        if (robots.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(robots, HttpStatus.OK);
    }

    // Get robot by ID or name (GET /api/robots/search)
    @GetMapping("/robots/search")
    public ResponseEntity<?> searchRobot(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String name) {

        if (id != null) {
            Robot robot = robotService.getRobotById(id);
            if (robot != null) {
                return new ResponseEntity<>(robot, HttpStatus.OK);
            }
        } else if (name != null) {
            Robot robot = robotService.getRobotByName(name);
            if (robot != null) {
                return new ResponseEntity<>(robot, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Get robot by ID (GET /api/robots/{id})
    @GetMapping("/robots/{id}")
    public ResponseEntity<Robot> getRobotById(@PathVariable int id) {
        Robot robot = robotService.getRobotById(id);
        if (robot != null) {
            return new ResponseEntity<>(robot, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Log new maintenance entry (PUT /api/robots/{id}/maintenance)
    @PutMapping("/robots/{id}/maintenance")
    public ResponseEntity<?> logRobotMaintenance(
            @PathVariable int id,
            @RequestBody Map<String, String> maintenanceData) {
        try {
            LocalDate lastServiceDate = LocalDate.parse(maintenanceData.get("lastServiceDate"));
            LocalDate nextServiceDate = LocalDate.parse(maintenanceData.get("nextServiceDate"));
            String partReplaced = maintenanceData.get("partReplaced");
            String technicianNotes = maintenanceData.get("technicianNotes");

            RobotMaintenance savedLog = robotService.logMaintenance(
                    id, lastServiceDate, nextServiceDate, partReplaced, technicianNotes
            );

            if (savedLog != null) {
                return new ResponseEntity<>(savedLog, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR: " + e.getMessage());
        }
    }

    // Update robot details (PUT /api/robots/{id})
    @PutMapping("/robots/{id}")
    public ResponseEntity<Robot> updateRobot(@PathVariable int id, @RequestBody Robot robot) {
        robot.setRobotID(id); // Ensure ID matches
        Robot updatedRobot = robotService.updateRobot(robot);
        if (updatedRobot != null) {
            return new ResponseEntity<>(updatedRobot, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete robot (DELETE /api/robots/{id})
    @DeleteMapping("/robots/{id}")
    public ResponseEntity<Map<String, String>> deleteRobot(@PathVariable int id) {
        boolean deleted = robotService.deleteRobot(id);
        Map<String, String> response = new HashMap<>();

        if (deleted) {
            response.put("message", "Robot deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", "Robot not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // Delete robot by search parameter (DELETE /api/robots/delete)
    @DeleteMapping("/robots/delete")
    public ResponseEntity<Map<String, String>> deleteRobotByParam(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String name) {

        Map<String, String> response = new HashMap<>();

        if (id != null) {
            boolean deleted = robotService.deleteRobot(id);
            if (deleted) {
                response.put("message", "Robot deleted successfully");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } else if (name != null) {
            Robot robot = robotService.getRobotByName(name);
            if (robot != null) {
                boolean deleted = robotService.deleteRobot(robot.getRobotID());
                if (deleted) {
                    response.put("message", "Robot deleted successfully");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        }

        response.put("message", "Robot not found");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Get full maintenance history for a robot (GET /api/robots/{id}/maintenance)
    @GetMapping("/robots/{id}/maintenance")
    public ResponseEntity<List<RobotMaintenance>> getMaintenanceHistory(@PathVariable int id) {
        List<RobotMaintenance> history = robotService.getMaintenanceHistory(id);
        if (history == null || history.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(history, HttpStatus.OK);
    }

    @GetMapping("/robot/test")
    public String test() {
        return "Robot API is working!";
    }

    @GetMapping("/overview")
    public List<Robot> getAllRobotOverview() {
        return robotRepository.findAll();
    }


}
