package org.example.projectlibrioo.Service.Admin;

import org.example.projectlibrioo.Model.Book;
import org.example.projectlibrioo.Repository.BookRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AdminService {

    @Autowired
    private BookRepo bookRepo;

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


//    public Book getAllBooks(int bookId) {
//        return bookRepo.findById(bookId);
//    }
}
