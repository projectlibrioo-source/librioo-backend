package org.example.projectlibrioo.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

@Data
@Entity
public class Book {
    @Id
    private int bookId;
    private long ISBN;
    private String title;
    private String category;
    private String author;
    @Lob
    private byte[] image;
    private int shelfNumber;
    private boolean availability;
}
