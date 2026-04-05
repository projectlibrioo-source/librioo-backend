package org.example.projectlibrioo.Service.Transactions;

import org.example.projectlibrioo.DTO.TransactionDTO;
import org.example.projectlibrioo.Model.Book;
import org.example.projectlibrioo.Model.Member;
import org.example.projectlibrioo.Model.Transactions;
import org.example.projectlibrioo.Repository.BookRepo;
import org.example.projectlibrioo.Repository.FineRepo;
import org.example.projectlibrioo.Repository.MemberRepo;
import org.example.projectlibrioo.Repository.TransactionRepo;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.example.projectlibrioo.Model.ReturnDTO;
import java.time.temporal.ChronoUnit;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepo transactionRepo;
    @Autowired
    private FineRepo fineRepo;
    @Autowired
    private MemberRepo memberRepo;
    @Autowired
    private BookRepo bookRepo;

    public Boolean checkEligibility(Transactions transactionBook){
        int libraryId = transactionBook.getLibraryId();
        String userCategory = transactionBook.getCategory();

        List<Transactions> bookDetails = transactionRepo.getBorrowedData(libraryId);
        int bookCount = bookDetails.size();

        org.example.projectlibrioo.Model.Fines fineConfig = fineRepo.findByCategory(userCategory);
        int loans = (fineConfig != null) ? fineConfig.getMax_loans() : 5;

        if(bookCount >= loans){
            return false;
        }

        else {
            for (Transactions transactions: bookDetails){
                if(transactions.getReturnDate().isBefore(LocalDate.now())){
                    return false;
                }
            }

            return true;
        }
    }

    public Transactions saveTransaction(Transactions transactionBook) {
        return transactionRepo.save(transactionBook);

    }

    public Double getFines(ReturnDTO returnBook) {
        double fine = 0.0;
        String userCategory = returnBook.getCategory();



        Transactions returnedData = transactionRepo.findByIds(returnBook.getLibraryId(),returnBook.getBookId());

        if(returnedData.getReturnDate().isBefore(LocalDate.now())){

            long overdueDays = ChronoUnit.DAYS.between(
                    returnedData.getReturnDate(),
                    LocalDate.now()
            );

            org.example.projectlibrioo.Model.Fines fineConfig = fineRepo.findByCategory(userCategory);
            double dailyRate = (fineConfig != null) ? fineConfig.getRate() : 0.0;

            fine = dailyRate * overdueDays;


        }

        else {
            fine = 0;

        }
        return fine;
    }

    public Transactions confirmReturn(ReturnDTO bookToReturn){
        Transactions returnedData = transactionRepo.findByIds(bookToReturn.getLibraryId(),bookToReturn.getBookId());
        returnedData.setStatus("Returned");

        return transactionRepo.save(returnedData);



    }




    public List<Transactions> getTransactionsByDate(LocalDate Date){
        return transactionRepo.findByBorrowDate(Date);
    }

    public List<Transactions> getTransactionsBetweenDates(LocalDate start, LocalDate end) {
        return transactionRepo.findByBorrowDateBetween(start, end);
    }

    public List<Transactions> getAllTransactions() {
        return transactionRepo.findAll();
    }


    public Transactions getAllUSers(int bookId) {
        return transactionRepo.findByBookId(bookId);
    }

    // Enriched transaction list for admin table (joins member name + book title)
    public List<TransactionDTO> getAllTransactionsEnriched() {
        List<Transactions> raw = transactionRepo.findAll();
        return raw.stream().map(tx -> {
            // Resolve member name
            String memberName = "User " + tx.getLibraryId();
            try {
                Member m = memberRepo.findUserByLibraryID(tx.getLibraryId());
                if (m != null) memberName = m.getFullName();
            } catch (Exception ignored) {}

            // Resolve book title
            String bookTitle = "Book " + tx.getBookId();
            String bookCategory = tx.getCategory();
            try {
                Book b = bookRepo.findById(tx.getBookId());
                if (b != null) {
                    bookTitle = b.getTitle();
                    if (bookCategory == null || bookCategory.isEmpty()) bookCategory = b.getCategory();
                }
            } catch (Exception ignored) {}

            return new TransactionDTO(
                tx.getTransactionId(),
                tx.getLibraryId(),
                memberName,
                tx.getBookId(),
                bookTitle,
                bookCategory,
                tx.getBorrowDate(),
                tx.getReturnDate(),
                tx.getStatus(),
                tx.getBorrowedThrough() != null ? tx.getBorrowedThrough() : "Counter"
            );
        }).collect(Collectors.toList());
    }

    // Count total transactions for a given user (for the Users table "Books Borrowed" column)
    public long countBorrowedByUser(int libraryId) {
        return transactionRepo.countByLibraryId(libraryId);
    }
}
