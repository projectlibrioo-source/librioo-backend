package org.example.projectlibrioo.Service.Admin;

import org.example.projectlibrioo.Model.Book;
import org.example.projectlibrioo.Model.Guest;
import org.example.projectlibrioo.Model.Member;
import org.example.projectlibrioo.Model.Robot;
import org.example.projectlibrioo.Repository.BookRepo;
import org.example.projectlibrioo.Repository.GuestRepo;
import org.example.projectlibrioo.Repository.MemberRepo;
import org.example.projectlibrioo.Repository.RobotRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private GuestRepo guestRepo;
    @Autowired
    private MemberRepo memberRepo;
    @Autowired
    private RobotRepo robotRepo;

    // save book
    public Book saveBookData(Book book, MultipartFile bookImage) throws Exception {
        book.setImage(bookImage.getBytes());
        return bookRepo.save(book);
    }

    public Book getAllBooks(int bookId) {
        Book book = bookRepo.findById(bookId);

        if (book != null){
            return book;
        }else {
            return null;
        }
    }

    // update book
    public Book updateBooks(Book book) {
        return bookRepo.save(book);
    }

    // delete book
    public Boolean deleteBooks(int bookId) {
        Book bookToDelete = bookRepo.findById(bookId);
        if (bookToDelete!=null){
            try{
                bookRepo.delete(bookToDelete);
                return true;

            }catch (Exception e){
                return false;
            }

        }else {
            return false;
        }
    }

    public Guest saveGuestData(Guest guest) {
        return guestRepo.save(guest);
    }

    public Member saveMemberData(Member member) {
        return memberRepo.save(member);
    }

    public Member getAllMembers(int memberId) {
        Member member = memberRepo.findUserByLibraryID(memberId);

        if (member != null){
            return member;
        }else {
            return null;
        }
    }

    public Member updateMember(Member member) {
        return memberRepo.save(member);
    }

    public Guest getAllGuests(int guestId) {
        Guest guest = guestRepo.findGuestByGuestID(guestId);

        if (guest != null){
            return guest;
        }else {
            return null;
        }
    }

    public Boolean deleteMember(int memberId) {
        Member memberToDelete = memberRepo.findUserByLibraryID(memberId);
        if (memberToDelete!=null){
            try{
                memberRepo.delete(memberToDelete);
                return true;

            }catch (Exception e){
                return false;
            }

        }else {
            return false;
        }
    }

    public Boolean deleteGuest(int guestId) {
        Guest guestToDelete = guestRepo.findGuestByGuestID(guestId);
        if (guestToDelete!=null){
            try{
                guestRepo.delete(guestToDelete);
                return true;

            }catch (Exception e){
                return false;
            }

        }else {
            return false;
        }
    }


    public Guest updateGuest(Guest guest) {
        return guestRepo.save(guest);
    }

    public List<Book> getAllBooksByKeyword(String title, String author, Long isbn) {
        return bookRepo.findAllBooksByKeyword(title,author,isbn);
    }

    public List<Member> getAllMembersByKeyword(String fullname, Integer libraryid) {
        return memberRepo.findAllMembersByKeyword(fullname,libraryid);
    }

    public Robot getAllRobotDetailsByKeyword(Integer robotid, String robotname) {
        return robotRepo.findRobotsByKeyword(robotid, robotname);
    }


//    public Book getAllBooks(int bookId) {
//        return bookRepo.findById(bookId);
//    }


    // get all books for the book page
    public List<Book> getAllBooks(){
        return bookRepo.findAll();
    }

    // get all users for the users page
    public List<Member> getAllMembers(){
        return memberRepo.findAll();
    }



}
