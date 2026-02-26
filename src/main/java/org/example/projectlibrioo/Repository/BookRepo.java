package org.example.projectlibrioo.Repository;

import org.example.projectlibrioo.Model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface BookRepo extends JpaRepository<Book, Integer> {

    @Query("""
    SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))""")
    List<Book> findAllByTitle(String title);

    @Query("SELECT B FROM Book B WHERE B.category= :category")
    List<Book> findBookByCategory(String category);

    @Query("SELECT DISTINCT B.category FROM Book B")
    List<String> findDistinctCategories();

    @Query("SELECT B.shelfNumber FROM Book B WHERE B.category= :category")
    List<Integer> findShelfNumberByCategory(String category);

    List<Book> findByCategoryIgnoreCase(String category);

    @Query("SELECT b FROM Book b WHERE b.bookId = :bookId")
    //Book findBook(@Param("bookId") int bookId);
    Book findById(int bookId);
}
