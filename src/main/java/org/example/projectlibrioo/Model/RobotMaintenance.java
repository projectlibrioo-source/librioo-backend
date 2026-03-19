package org.example.projectlibrioo.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "robot_maintenance")
public class RobotMaintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "robot_id", nullable = false)
    private Robot robot;

    private LocalDate lastServiceDate;
    private LocalDate nextServiceDate;
    private String partReplaced;

    @Column(length = 1000)
    private String technicianNotes;

    private LocalDate loggedAt;

    @PrePersist
    public void prePersist() {
        this.loggedAt = LocalDate.now();
    }
}
