package org.example.projectlibrioo.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SystemReportRowDTO {
    private LocalDate date;
    private String robotId;          // e.g., "Robot Alpha"
    private String eventsLogged;      // e.g., "4 Err, 12 Warn"
    private double avgBattery;        // percentage
}