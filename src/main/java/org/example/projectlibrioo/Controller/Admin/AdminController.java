package org.example.projectlibrioo.Controller.Admin;

import org.example.projectlibrioo.Model.Book;
import org.example.projectlibrioo.Model.Member;
import org.example.projectlibrioo.Service.Admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class AdminController {
    @Autowired
    private AdminService adminService;

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


}
