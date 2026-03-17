package org.example.projectlibrioo.Service.Admin;

import org.example.projectlibrioo.Model.Book;
import org.example.projectlibrioo.Model.Member;
import org.example.projectlibrioo.Repository.BookRepo;
import org.example.projectlibrioo.Repository.MemberRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private MemberRepo memberRepo;

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

    public Member saveMemberData(Member member) {
        return memberRepo.save(member);
    }


//    public Book getAllBooks(int bookId) {
//        return bookRepo.findById(bookId);
//    }


    // get all books for the book page
    public List<Book> getAllBooks(){
        return bookRepo.findAll();
    }





}
