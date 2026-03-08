package org.example.projectlibrioo.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Robot {
    @Id
    private int robotID;
    private String robotName;
    private LocalDate startDate;
    private LocalDate serviceDate;
    private String partReplaced;
    private String status;
}
