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
//import com.fasterxml.jackson.databind.ObjectMapper;

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
            return new ResponseEntity<>(returnedBook, HttpStatus.OK);
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



    @PostMapping("/borrowbook")
    public ResponseEntity<Transactions> borrowBook(@RequestBody Transactions transactionBook){
        Boolean bookBorrowed = transactionService.checkEligibility(transactionBook);

        if (bookBorrowed){
            return new ResponseEntity<>
                    (transactionService.saveTransaction(transactionBook),HttpStatus.OK);

        }
        else {
            return new  ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/getfines")
    public ResponseEntity<Double> calculateFines(@RequestBody
    ReturnDTO returnBook){
        double fine = transactionService.getFines(returnBook);

        return new ResponseEntity<>(fine,HttpStatus.OK);
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
    public ResponseEntity<Member> updateMembers(@RequestBody Member member){
        Member updatedMember = adminService.updateMember(member);

        if (updatedMember != null){
            return new ResponseEntity<>(updatedMember, HttpStatus.OK);
        }else {
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

    public String test2(){
        return "test2";
    }

    //Get all transactions
    @GetMapping("/transactions")
    public List<Transactions> getAllTransactions(){
        return transactionService.getAllTransactions();
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

    // Get transaction by specific date
    @GetMapping("/transactions/{date}")
    public List<Transactions> getTransactionsByDate(@PathVariable String date) {
        return transactionService.getTransactionsByDate(LocalDate.parse(date));
    }

    @GetMapping("/transactions/search")
    public List<Transactions> searchBetweenDates(@RequestParam String start, @RequestParam String end) {
        return transactionService.getTransactionsBetweenDates(LocalDate.parse(start), LocalDate.parse(end));
    }

    // get all books for the book page
    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAllBooks(){
        List<Book> books = adminService.getAllBooks();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }


    // get all users for users page
    @GetMapping("/users")
    public ResponseEntity<List<Member>> getAllMembers(){
        List<Member> members = adminService.getAllMembers();
        return new ResponseEntity<>(members, HttpStatus.OK);
    }


}