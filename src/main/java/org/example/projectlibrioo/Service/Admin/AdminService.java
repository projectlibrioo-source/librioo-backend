package org.example.projectlibrioo.Service.Admin;

import org.example.projectlibrioo.Model.Book;
import org.example.projectlibrioo.Model.Guest;
import org.example.projectlibrioo.Repository.BookRepo;
import org.example.projectlibrioo.Repository.GuestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AdminService {

    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private GuestRepo guestRepo;

    public Book saveBookData(Book book, MultipartFile bookImage) throws Exception {
        book.setImage(bookImage.getBytes());
        return bookRepo.save(book);
    }

    public Guest saveGuestData(Guest guest) {
        return guestRepo.save(guest);
    }


}
