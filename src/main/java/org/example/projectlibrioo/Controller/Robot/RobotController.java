package org.example.projectlibrioo.Controller.Robot;

import org.example.projectlibrioo.Model.Robot;
import org.example.projectlibrioo.Service.RobotService.RobotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RobotController {

    @Autowired
    private RobotService robotService;

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

    @GetMapping("/robot/test")
    public String test() {
        return "Robot API is working!";
    }


}
