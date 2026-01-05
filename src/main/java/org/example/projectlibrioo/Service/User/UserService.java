package org.example.projectlibrioo.Service.User;

import org.example.projectlibrioo.Model.Book;
import org.example.projectlibrioo.Model.Guest;
import org.example.projectlibrioo.Model.Member;
import org.example.projectlibrioo.Repository.BookRepo;
import org.example.projectlibrioo.Repository.GuestRepo;
import org.example.projectlibrioo.Repository.MemberRepo;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private MemberRepo memberRepo;
    @Autowired
    private GuestRepo guestRepo;

    public Member checkLibraryId(int libraryId) {
        return memberRepo.findUserByLibraryID(libraryId);
    }

    public List<Book> getBookByName(String bookName) {
        return bookRepo.findAllByTitle(bookName);
    }

    public Guest checkGuestId(int guestId) {
        return guestRepo.findGuestByGuestID(guestId);
    }

    public List<String> getCategories() {
        return bookRepo.findDistinctCategories();
    }

    public List<Integer> searchByCategory(String category) {
        return bookRepo.findShelfNumberByCategory(category);
    }
}
