package org.example.projectlibrioo.Controller.User;

import org.example.projectlibrioo.Model.Book;
import org.example.projectlibrioo.Model.Guest;
import org.example.projectlibrioo.Model.Member;
import org.example.projectlibrioo.Service.Admin.AdminService;
import org.example.projectlibrioo.Service.User.UserService;
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
}
