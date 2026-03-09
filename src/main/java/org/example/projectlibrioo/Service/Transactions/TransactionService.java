package org.example.projectlibrioo.Service.Transactions;

import org.example.projectlibrioo.Model.Transactions;
import org.example.projectlibrioo.Repository.FineRepo;
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
    @Autowired
    private FineRepo fineRepo;

    public Boolean checkEligibility(Transactions transactionBook){
        int libraryId = transactionBook.getLibraryId();
        String userCategory = transactionBook.getCategory();

        List<Transactions> bookDetails = transactionRepo.getBorrowedData(libraryId);
        int bookCount = bookDetails.toArray().length;

        int loans = fineRepo.findMaxLoanByCategory(userCategory);

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

            fine = fineRepo.findFineByCategory(userCategory) * overdueDays;


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


    public TransactionService(TransactionRepo transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    public List<Transactions> getTransactionsByDate(LocalDate Date){
        return transactionRepo.findByBorrowDate(Date);
    }


    public Transactions getAllUSers(int bookId) {
        return transactionRepo.findByBookId(bookId);
    }
}
