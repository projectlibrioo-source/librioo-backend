package org.example.projectlibrioo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private int id;           // libraryID
    private String fullName;
    private String email;
    private String phoneNumber;
    private String status;
    private String role;      // "Member" or "Guest"
    private String userType;  // "Student", "Lecturer"
    private int booksBorrowed; // count from Transactions table
}
