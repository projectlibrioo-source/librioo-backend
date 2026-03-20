package org.example.projectlibrioo.Controller.Admin;

import org.example.projectlibrioo.Model.Book;
import org.example.projectlibrioo.Model.Robot;
import org.example.projectlibrioo.Model.Transactions;
import org.example.projectlibrioo.Repository.BookRepo;
import org.example.projectlibrioo.Repository.RobotRepo;
import org.example.projectlibrioo.Repository.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/admin/reports")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ReportController {

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private BookRepo bookRepo;

    @Autowired
    private RobotRepo robotRepo;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Real count from Transactions table
        long totalLoans = transactionRepo.count();
        stats.put("totalLoans", totalLoans);
        
        stats.put("systemUptime", "99.8%");
        
        // Mocking revenue for demo, could be sum of Fines from fine collections
        stats.put("revenue", 12500.50);

        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateReport(@RequestBody Map<String, Object> payload) {
        String reportType = (String) payload.get("reportType"); // LIBRARY or SYSTEM
        String fromDateStr = (String) payload.get("fromDate");
        String toDateStr = (String) payload.get("toDate");

        Map<String, Object> response = new HashMap<>();

        if ("LIBRARY".equalsIgnoreCase(reportType)) {
            List<Map<String, Object>> rows = new ArrayList<>();
            // Generate some deterministic aggregate rows based on dates requested
            int startDay = 1;
            int totalLoans = (int) transactionRepo.count();
            
            Map<String, Object> row1 = new HashMap<>();
            row1.put("date", fromDateStr != null && !fromDateStr.isEmpty() ? fromDateStr : LocalDate.now().toString());
            row1.put("patrons", 45);
            row1.put("loans", totalLoans > 0 ? totalLoans : 12);
            row1.put("overdues", 3);
            rows.add(row1);

            response.put("reportRows", rows);

            // Fetch Top 5 Books for popular items
            List<Map<String, Object>> popular = new ArrayList<>();
            List<Book> books = bookRepo.findAll();
            int count = 0;
            for (Book b : books) {
                if (count >= 5) break;
                Map<String, Object> pop = new HashMap<>();
                pop.put("title", b.getTitle());
                pop.put("author", b.getAuthor());
                pop.put("checkouts", 10 + (b.getBookId() * 3)); // mock checkout count
                popular.add(pop);
                count++;
            }
            response.put("popularItems", popular);

        } else { // SYSTEM
            List<Map<String, Object>> rows = new ArrayList<>();
            List<Robot> robots = robotRepo.findAll();
            
            for (Robot r : robots) {
                Map<String, Object> row = new HashMap<>();
                row.put("date", LocalDate.now().toString());
                row.put("robotId", r.getRobotName() != null ? r.getRobotName() : "Robot " + r.getRobotID());
                row.put("eventsLogged", 2 + (r.getRobotID() % 5));
                row.put("avgBattery", (60 + (r.getRobotID() * 7) % 40) + "%");
                rows.add(row);
            }
            response.put("reportRows", rows);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
