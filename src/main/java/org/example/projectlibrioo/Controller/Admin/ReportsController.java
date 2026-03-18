package org.example.projectlibrioo.Controller.Admin;

import org.example.projectlibrioo.DTO.LibraryReportResponseDTO;
import org.example.projectlibrioo.DTO.SystemReportResponseDTO;
import org.example.projectlibrioo.Service.Admin.ReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.example.projectlibrioo.DTO.DashboardStatsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.example.projectlibrioo.DTO.ReportRequestDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/admin/reports")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ReportsController {

    @Autowired
    private ReportsService reportsService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        DashboardStatsDTO stats = reportsService.getDashboardStats();
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateReport(@RequestBody ReportRequestDTO request) {
        try {
            if ("LIBRARY".equalsIgnoreCase(request.getReportType())) {
                LibraryReportResponseDTO report = reportsService.generateLibraryReport(request);
                return new ResponseEntity<>(report, HttpStatus.OK);
            } else if ("SYSTEM".equalsIgnoreCase(request.getReportType())) {
                SystemReportResponseDTO report = reportsService.generateSystemReport(request);
                return new ResponseEntity<>(report, HttpStatus.OK);
            } else {
                return ResponseEntity.badRequest().body("Invalid report type");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating report: " + e.getMessage());
        }
    }
}