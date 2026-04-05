package org.example.projectlibrioo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private int transactionId;
    private int libraryId;
    private String memberName;   // Joined from Member table
    private int bookId;
    private String bookTitle;    // Joined from Book table
    private String bookCategory;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private String status;
    private String borrowedThrough; // e.g. "Robot", "Counter"
}
