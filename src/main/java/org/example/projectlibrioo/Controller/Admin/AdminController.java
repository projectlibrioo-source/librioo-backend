package org.example.projectlibrioo.Controller.Admin;
import org.example.projectlibrioo.Model.*;
import org.example.projectlibrioo.Service.Admin.AdminService;
import org.example.projectlibrioo.Service.Transactions.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private TransactionService transactionService;


    /*@PostMapping("/addbook")
    public ResponseEntity<Book> addBook(@RequestPart("book") Book book, @RequestPart("bookImage") MultipartFile bookImage) throws Exception{
        Book bookSaved = adminService.saveBookData(book, bookImage);

        if (bookSaved!=null){
            return new ResponseEntity<>(bookSaved, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
   @PostMapping("/addbook")
    public ResponseEntity<?> addBook(
            @RequestPart("book") String bookJson,
            @RequestPart("bookImage") MultipartFile bookImage) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            Book book = mapper.readValue(bookJson, Book.class);

            Book bookSaved = adminService.saveBookData(book, bookImage);
            return ResponseEntity.ok(bookSaved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR: " + e.getMessage());
        }
    }*/

    //Manage books
    @PostMapping("/addbook")
    public ResponseEntity<?> addBook(
            @RequestPart("book") String bookJson,  // ✅ Accept as String
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

    @GetMapping("/getallbooks")
    public ResponseEntity<Book> getAllBooks(@RequestParam("bookid") int bookId){
        Book returnedBook = adminService.getAllBooks(bookId);
        if (returnedBook == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(returnedBook, HttpStatus.FOUND);
        }

    }

    @PutMapping("/updatebook")
    public ResponseEntity<Book> updateBooks(@RequestBody Book book){
        Book updatedBook = adminService.updateBooks(book);

        if (updatedBook != null){
            return new ResponseEntity<>(updatedBook, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/deletebook")
    public ResponseEntity<String> deleteBooks(@RequestParam("bookid") int bookId){
        Boolean bookDeleted = adminService.deleteBooks(bookId);

        if (bookDeleted){
            return new ResponseEntity<>("Book deleted successfully",HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
    }

    //Manage Users
    @PostMapping("/addmember")
    public ResponseEntity<?> addMember(@RequestBody Member member) {
        try {

            Member memberSaved = adminService.saveMemberData(member);

            if (memberSaved != null) {
                return new ResponseEntity<>(memberSaved,HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public String test() {
        return "API is working!";
    }


    @PostMapping("/borrowbook")
    public ResponseEntity<Transactions> borrowBook(@RequestBody Transactions transactionBook){
        Boolean bookBorrowed = transactionService.checkEligibility(transactionBook);

        if (bookBorrowed){
            transactionBook.setStatus("Borrowed");
            return new ResponseEntity<>(transactionService.saveTransaction(transactionBook),HttpStatus.OK);

        }
        else {
            return new  ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/getusers")
    public ResponseEntity<Transactions> getAllUsers(@RequestParam("bookid") int bookId){
        Transactions foundUser = transactionService.getAllUSers(bookId);

        if (foundUser == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(foundUser, HttpStatus.OK);
        }
    }

    @PostMapping("/getfines")
    public ResponseEntity<Double> calculateFines(@RequestBody ReturnDTO returnBook){
        double fine = transactionService.getFines(returnBook);

        return new ResponseEntity<>(fine,HttpStatus.OK);
    }

    @PutMapping("/confirmreturn")
    public ResponseEntity<Transactions> confirmTheReturn(@RequestBody ReturnDTO returnDTO){
        Transactions bookReturned = transactionService.confirmReturn(returnDTO);

        return new ResponseEntity<>(bookReturned, HttpStatus.OK);
    }

    @GetMapping("/searchbook")
    public ResponseEntity<List<Book>> getAllBooks(@RequestParam(required = false) String title,
                                                  @RequestParam(required = false) String author,
                                                  @RequestParam(required = false) Long isbn){

        List<Book> listOfBooks = adminService.getAllBooksByKeyword(title,author,isbn);
         if (listOfBooks!=null){
             return new ResponseEntity<>(listOfBooks, HttpStatus.OK);
         }else {
             return new ResponseEntity<>(HttpStatus.NOT_FOUND);
         }
    }

    @GetMapping("/searchmember")
    public ResponseEntity<List<Member>> getAllUsers(@RequestParam(required = false) String fullname,
                                                  @RequestParam(required = false) Integer libraryid){

        List<Member> listOfMembers = adminService.getAllMembersByKeyword(fullname,libraryid);
        if (listOfMembers!=null){
            return new ResponseEntity<>(listOfMembers, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/searchrobot")
    public ResponseEntity<Robot> getAllRobotDetails(@RequestParam(required = false) Integer robotid,
                                                    @RequestParam(required = false) String robotname){

        Robot RobotDetails = adminService.getAllRobotDetailsByKeyword(robotid,robotname);
        if (RobotDetails!=null){
            return new ResponseEntity<>(RobotDetails, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
