package org.example.projectlibrioo.Repository;

import org.example.projectlibrioo.Model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface BookRepo extends JpaRepository<Book, Integer> {

    List<Book> findAllByTitle(String title);

    @Query("SELECT B FROM Book B WHERE B.category= :category")
    List<Book> findBookByCategory(String category);

}
