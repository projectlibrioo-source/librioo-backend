package org.example.projectlibrioo.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
public class Robot {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int robotID;
    private String robotName;
    private String model;
    private String shelfLocation;
    private LocalDate startDate;
    private LocalDate lastServiceDate;
    private LocalDate nextServiceDate;
    private String partReplaced;
    private String technicianNotes;
    private String status; // ACTIVE, MAINTENANCE, RETIRED
}
