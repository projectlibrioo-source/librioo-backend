package org.example.projectlibrioo.Controller.Admin;

import org.example.projectlibrioo.Model.Book;
import org.example.projectlibrioo.Service.Admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/addbook")
    public ResponseEntity<Book> addBook(@RequestPart Book book, @RequestPart MultipartFile bookImage) throws Exception{
        Book bookSaved = adminService.saveBookData(book, bookImage);

        if (bookSaved!=null){
            return new ResponseEntity<>(bookSaved, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
