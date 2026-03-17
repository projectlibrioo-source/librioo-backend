package org.example.projectlibrioo.Model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardDTO {

    private long totalBooks;
    private long totalUsers;
    private long activeUsers;
    private long booksBorrowed;
    private long overdueBooks;
    private long activeRobots;

}
