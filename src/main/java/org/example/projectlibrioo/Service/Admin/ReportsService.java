package org.example.projectlibrioo.Service.Admin;

import org.springframework.stereotype.Service;
import org.example.projectlibrioo.Repository.BookRepo;
import org.example.projectlibrioo.Repository.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;

import org.example.projectlibrioo.DTO.DashboardStatsDTO;

@Service
public class ReportsService {
    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private BookRepo bookRepo;

    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        stats.setTotalLoans(1234);
        stats.setSystemUptime(99.9);
        stats.setRevenue(2500.0);
        return stats;
    }
}