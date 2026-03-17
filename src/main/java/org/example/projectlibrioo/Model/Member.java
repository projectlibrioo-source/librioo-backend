package org.example.projectlibrioo.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Member {
    @Id
    private int libraryID;
    private String fullName;
    private String address;
    private String occupation;
    private String workOrSchoolAddress;
    private String email;
    private String phoneNumber;
    private int age;
    private String NICNumber;
    private String status;
}
