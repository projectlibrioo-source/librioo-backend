package org.example.projectlibrioo.Controller.Admin;

import org.example.projectlibrioo.Model.Admin;
import org.example.projectlibrioo.Model.Fines;
import org.example.projectlibrioo.Repository.AdminRepo;
import org.example.projectlibrioo.Repository.FineRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SettingsController {

    @Autowired
    private FineRepo fineRepo;

    @Autowired
    private AdminRepo adminRepo;

    // --- FINES / CIRCULATION RULES ---

    @GetMapping("/fines")
    public ResponseEntity<List<Fines>> getAllFines() {
        return new ResponseEntity<>(fineRepo.findAll(), HttpStatus.OK);
    }

    @PostMapping("/fines")
    public ResponseEntity<?> saveOrUpdateFineCategory(@RequestBody Fines fine) {
        try {
            // Check if category already exists, if so update it, else save new
            List<Fines> allFines = fineRepo.findAll();
            Fines existing = null;
            for (Fines f : allFines) {
                if (f.getCategory().equalsIgnoreCase(fine.getCategory())) {
                    existing = f;
                    break;
                }
            }
            if (existing != null) {
                existing.setMax_loans(fine.getMax_loans());
                existing.setRate(fine.getRate());
                fineRepo.save(existing);
                return new ResponseEntity<>(existing, HttpStatus.OK);
            } else {
                Fines saved = fineRepo.save(fine);
                return new ResponseEntity<>(saved, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // --- ADMIN USERS ---

    @GetMapping("/admins")
    public ResponseEntity<List<Admin>> getAllAdmins() {
        return new ResponseEntity<>(adminRepo.findAll(), HttpStatus.OK);
    }

    @PostMapping("/admins")
    public ResponseEntity<?> createAdmin(@RequestBody Admin admin) {
        try {
            Admin existing = adminRepo.findByAdminUsername(admin.getAdminUsername());
            if (existing != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Admin Username already exists!");
            }
            Admin saved = adminRepo.save(admin);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
