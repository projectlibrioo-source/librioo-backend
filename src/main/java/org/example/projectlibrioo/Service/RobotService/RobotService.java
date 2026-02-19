package org.example.projectlibrioo.Service.RobotService;

import org.example.projectlibrioo.navigation.ShelfPathMap;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
@Service
public class RobotService {
    private final ShelfPathMap shelfPathMap;
    private final RestTemplate restTemplate = new RestTemplate();

    public RobotService(ShelfPathMap shelfPathMap) {
        this.shelfPathMap = shelfPathMap;
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
}
