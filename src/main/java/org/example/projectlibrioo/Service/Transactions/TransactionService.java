package org.example.projectlibrioo.Service.Transactions;

import org.example.projectlibrioo.Model.Transactions;
import org.example.projectlibrioo.Repository.TransactionRepo;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.example.projectlibrioo.Model.ReturnDTO;
import java.time.temporal.ChronoUnit;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepo transactionRepo;

    public Boolean checkEligibility(Transactions transactionBook){
        int libraryId = transactionBook.getLibraryId();
        List<Transactions> bookDetails = transactionRepo.getBorrowedData(libraryId);
        int bookCount = bookDetails.toArray().length;

        if(bookCount >= 5){
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
        double fine =0.0;

        Transactions returnedData = transactionRepo.findByIds(returnBook.getLibraryId(),returnBook.getBookId());

        if(returnedData.getReturnDate().isBefore(LocalDate.now())){

            long overdueDays = ChronoUnit.DAYS.between(
                    returnedData.getReturnDate(),
                    LocalDate.now()
            );
            fine = overdueDays * 10;

        }

        else {
            fine = 0;

        }
        return fine;
    }

    public TransactionService(TransactionRepo transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    public List<Transactions> getTransactionsByDate(LocalDate Date){
        return transactionRepo.findByBorrowDate(Date);
    }


}
