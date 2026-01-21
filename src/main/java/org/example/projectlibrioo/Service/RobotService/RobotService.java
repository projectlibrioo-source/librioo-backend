package org.example.projectlibrioo.Service.RobotService;

import java.util.List;

import org.example.projectlibrioo.navigation.ShelfPathMap;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
public class RobotService {
    private final ShelfPathMap shelfPathMap;
    private final RestTemplate restTemplate = new RestTemplate();

    public RobotService(ShelfPathMap shelfPathMap) {
        this.shelfPathMap = shelfPathMap;
    }

    public void navigateToShelf(int shelfNumber) {

        List<String> path = shelfPathMap.getPath(shelfNumber);
        String command = String.join(",", path);

        String url = "http://192.168.4.1/move?cmd=" + command;
        restTemplate.getForObject(url, String.class);
    }
}
