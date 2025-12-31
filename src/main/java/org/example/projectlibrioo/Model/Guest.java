package org.example.projectlibrioo.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Guest {
    @Id
    private int guestID;
    private String fullName;
    private String address;
    private String email;
    private String phoneNumber;
    private int age;
    private String NICNumber;
}
