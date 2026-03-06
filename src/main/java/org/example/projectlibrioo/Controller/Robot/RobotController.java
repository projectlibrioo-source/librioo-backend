package org.example.projectlibrioo.Controller.Robot;

import org.example.projectlibrioo.Service.RobotService.RobotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class RobotController {
    @Autowired
    private RobotService robotService;
}
