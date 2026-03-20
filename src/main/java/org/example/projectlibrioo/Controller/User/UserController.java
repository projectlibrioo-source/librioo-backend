package org.example.projectlibrioo.Controller.User;

import org.example.projectlibrioo.Model.*;
import org.example.projectlibrioo.Repository.BookRepo;
import org.example.projectlibrioo.Service.Admin.AdminService;
import org.example.projectlibrioo.Service.FirebaseService;
import org.example.projectlibrioo.Service.RobotService.RobotService;
import org.example.projectlibrioo.Service.Transactions.TransactionService;
import org.example.projectlibrioo.Service.User.UserService;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private RobotService robotService;
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private FirebaseService firebaseService;


    @PostMapping("/loginmember")
    public ResponseEntity<Member> loginAsMember(@RequestParam("libraryid") int libraryId){
        Member member = userService.checkLibraryId(libraryId);

        if (member == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }else {
            return new ResponseEntity<>(member, HttpStatus.OK);
        }
    }

    @PostMapping("/loginguest")
    public ResponseEntity<Guest> loginAsGuest(@RequestParam("guestid") int guestId){
        Guest guest = userService.checkGuestId(guestId);

        if (guest == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }else {
            return new ResponseEntity<>(guest,HttpStatus.OK);
        }

    }

    @GetMapping("/searchname")
    public ResponseEntity<List<Book>> searchByBookName(@RequestParam("keyword") String bookName){
        return new ResponseEntity<>(userService.getBookByName(bookName), HttpStatus.OK);
    }

    @GetMapping("/getcategory")
    public ResponseEntity<List<String>> getAllCategories(){
        List<String> listOfCategories = userService.getCategories();

        if (listOfCategories == null){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }else {
            return new ResponseEntity<>(listOfCategories,HttpStatus.OK);
        }
    }

    @GetMapping("/searchcategory")
    public ResponseEntity<List<Integer>> searchByCategory(@RequestParam("category") String category){
        return new ResponseEntity<List<Integer>>(userService.searchByCategory(category),HttpStatus.OK);
    }

    @GetMapping("/navigate/book")
    public ResponseEntity<String> navigateByBookName(
            @RequestParam("name") String bookName) {

        List<Book> books = userService.getBookByName(bookName);

        if (books.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Book not found");
        }

        int shelfNumber = books.get(0).getShelfNumber();
        robotService.navigateToShelf(shelfNumber);

        // ✅ Send shelf number to Firebase Realtime Database
        firebaseService.sendShelfNumber(shelfNumber);

        return ResponseEntity.ok("Robot navigating to shelf " + shelfNumber);
    }


    @GetMapping("/navigate/category")
    public ResponseEntity<String> navigateByCategory(
            @RequestParam("category") String category) {

        List<Book> books = userService.getBooksByCategory(category);

        if (books.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("No books in this category");
        }

        int shelfNumber = books.get(0).getShelfNumber();
        robotService.navigateToShelf(shelfNumber);

        return ResponseEntity.ok("Robot navigating to shelf " + shelfNumber);
    }

    @PostMapping("/borrowrobot")
    public ResponseEntity<Transactions> borrowBookWithRobot(@RequestBody Transactions borrowRequest){
        Boolean bookBorrowed = transactionService.checkEligibility(borrowRequest);

        if (bookBorrowed){
            return new ResponseEntity<>(transactionService.saveTransaction(borrowRequest), HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }


}
