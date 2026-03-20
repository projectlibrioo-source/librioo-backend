package org.example.projectlibrioo.Controller.Admin;

import org.example.projectlibrioo.Model.Notification;
import org.example.projectlibrioo.Repository.NotificationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class NotificationController {

    @Autowired
    private NotificationRepo notificationRepo;

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return new ResponseEntity<>(notificationRepo.findAll(), HttpStatus.OK);
    }

    @PutMapping("/read-all")
    public ResponseEntity<String> markAllAsRead() {
        List<Notification> all = notificationRepo.findAll();
        for (Notification n : all) {
            n.setRead(true);
        }
        notificationRepo.saveAll(all);
        return new ResponseEntity<>("All marked as read", HttpStatus.OK);
    }

    @PostMapping("/test")
    public ResponseEntity<Notification> sendTestAlert() {
        Notification n = new Notification();
        n.setType("system");
        n.setTitle("Test Alert");
        n.setText("This is a manually triggered test alert to verify the notification system is working.");
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, hh:mma");
        n.setDateStr(dtf.format(LocalDateTime.now()));
        n.setRead(false);
        
        notificationRepo.save(n);
        return new ResponseEntity<>(n, HttpStatus.CREATED);
    }
}
