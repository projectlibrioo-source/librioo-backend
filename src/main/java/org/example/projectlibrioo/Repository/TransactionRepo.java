package org.example.projectlibrioo.Repository;

import org.example.projectlibrioo.Model.Transactions;
import org.hibernate.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transactions,Integer> {
    @Query("SELECT t FROM Transactions t WHERE t.libraryId = :libraryId AND t.status = 'Borrowed' ")
    List<Transactions> getBorrowedData(int libraryId);

    @Query("SELECT r FROM Transactions r WHERE r.libraryId = :libraryId AND r.bookId = :bookId")
    Transactions findByIds(int libraryId, int bookId);


}


