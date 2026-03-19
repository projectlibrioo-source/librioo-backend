package org.example.projectlibrioo.Controller.Admin;

import org.example.projectlibrioo.Model.Book;
import org.example.projectlibrioo.Model.Guest;
import org.example.projectlibrioo.Model.Member;
import org.example.projectlibrioo.Model.Transactions;
import org.example.projectlibrioo.Service.Admin.AdminService;
import org.example.projectlibrioo.Service.Transactions.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private TransactionService transactionService;

    @PostMapping("/addbook")
    public ResponseEntity<?> addBook(
            @RequestPart("book") String bookJson,
            @RequestPart("bookImage") MultipartFile bookImage) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            Book book = mapper.readValue(bookJson, Book.class);

            Book bookSaved = adminService.saveBookData(book, bookImage);

            if (bookSaved != null) {
                return ResponseEntity.ok(bookSaved);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR: " + e.getMessage());
        }
    }

    @PostMapping("/addmember")
    public ResponseEntity<?> addMember(@RequestBody Member member) {
        try {
            Member memberSaved = adminService.saveMemberData(member);

            if (memberSaved != null) {
                return new ResponseEntity<>(memberSaved, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR: " + e.getMessage());
        }
    }

    @PostMapping("/addguest")
    public ResponseEntity<?> addGuest(@RequestBody Guest guest) {
        try {
            Guest guestSaved = adminService.saveGuestData(guest);

            if (guestSaved != null) {
                return new ResponseEntity<>(guestSaved, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR: " + e.getMessage());
        }
    }

    @GetMapping("/getallmembers")
    public ResponseEntity<Member> getAllMembers(@RequestParam("memberid") int memberId) {
        Member returnedMember = adminService.getAllMembers(memberId);
        if (returnedMember == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(returnedMember, HttpStatus.OK); // Changed from FOUND to OK
        }
    }

    @PutMapping("/updatemember")
    public ResponseEntity<Member> updateMembers(@RequestBody Member member) {
        Member updatedMember = adminService.updateMember(member);

        if (updatedMember != null) {
            return new ResponseEntity<>(updatedMember, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getallguests")
    public ResponseEntity<Guest> getAllGuests(@RequestParam("guestid") int guestId) {
        Guest returnedGuest = adminService.getAllGuests(guestId);
        if (returnedGuest == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(returnedGuest, HttpStatus.OK); // Changed from FOUND to OK
        }
    }

    @PutMapping("/updateguest")
    public ResponseEntity<Guest> updateGuests(@RequestBody Guest guest) {
        Guest updatedGuest = adminService.updateGuest(guest);

        if (updatedGuest != null) {
            return new ResponseEntity<>(updatedGuest, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/deletemember")
    public ResponseEntity<String> deleteMembers(@RequestParam("memberid") int memberId) {
        Boolean memberDeleted = adminService.deleteMember(memberId);

        if (memberDeleted) {
            return new ResponseEntity<>("Member deleted successfully", HttpStatus.OK); // Fixed message
        } else {
            return new ResponseEntity<>("Member not found", HttpStatus.NOT_FOUND); // Better error message
        }
    }

    @DeleteMapping("/deleteguest")
    public ResponseEntity<String> deleteGuests(@RequestParam("guestid") int guestId) {
        Boolean guestDeleted = adminService.deleteGuest(guestId);

        if (guestDeleted) {
            return new ResponseEntity<>("Guest deleted successfully", HttpStatus.OK); // Fixed message
        } else {
            return new ResponseEntity<>("Guest not found", HttpStatus.NOT_FOUND); // Better error message
        }
    }

    @GetMapping("/test")
    public String test() {
        return "API is working!";
    }

    // Get all transactions
    @GetMapping("/transactions")
    public List<Transactions> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    // Get transaction by specific date
    @GetMapping("/transactions/{date}")
    public List<Transactions> getTransactionsByDate(@PathVariable String date) {
        return transactionService.getTransactionsByDate(LocalDate.parse(date));
    }

    @GetMapping("/transactions/search")
    public List<Transactions> searchBetweenDates(@RequestParam String start, @RequestParam String end) {
        return transactionService.getTransactionsBetweenDates(LocalDate.parse(start), LocalDate.parse(end));
    }
}