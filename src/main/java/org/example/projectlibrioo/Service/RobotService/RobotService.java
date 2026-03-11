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

        String url = "http://10.102.165.232/send?num=" + shelfNumber;
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

}
