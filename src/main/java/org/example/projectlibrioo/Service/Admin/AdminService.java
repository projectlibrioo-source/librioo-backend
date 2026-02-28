package org.example.projectlibrioo.Service.Admin;

import org.example.projectlibrioo.Model.Book;
import org.example.projectlibrioo.Model.Guest;
import org.example.projectlibrioo.Model.Member;
import org.example.projectlibrioo.Repository.BookRepo;
import org.example.projectlibrioo.Repository.GuestRepo;
import org.example.projectlibrioo.Repository.MemberRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AdminService {

    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private GuestRepo guestRepo;
    @Autowired
    private MemberRepo memberRepo;

    public Book saveBookData(Book book, MultipartFile bookImage) throws Exception {
        book.setImage(bookImage.getBytes());
        return bookRepo.save(book);
    }

    public Guest saveGuestData(Guest guest) {
        return guestRepo.save(guest);
    }

    public Member getAllMembers(int memberId) {
        Member member = memberRepo.findUserByLibraryID(memberId);

        if (member != null){
            return member;
        }else {
            return null;
        }
    }


}
