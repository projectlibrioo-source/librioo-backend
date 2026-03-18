package org.example.projectlibrioo.Service.Admin;

import org.springframework.stereotype.Service;
import org.example.projectlibrioo.Repository.BookRepo;
import org.example.projectlibrioo.Repository.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.projectlibrioo.DTO.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.example.projectlibrioo.DTO.DashboardStatsDTO;

@Service
public class ReportsService {
    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private BookRepo bookRepo;

    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        stats.setTotalLoans(transactionRepo.count());
        stats.setSystemUptime(99.9);
        stats.setRevenue(2500.0);
        return stats;
    }

    public LibraryReportResponseDTO generateLibraryReport(ReportRequestDTO request) {
        LibraryReportResponseDTO response = new LibraryReportResponseDTO();
        List<LibraryReportRowDTO> rows = new ArrayList<>();

        LocalDate start = request.getFromDate() != null ? request.getFromDate() : LocalDate.now().minusDays(7);
        LocalDate end = request.getToDate() != null ? request.getToDate() : LocalDate.now();

        // Dummy data
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            LibraryReportRowDTO row = new LibraryReportRowDTO();
            row.setDate(date);
            row.setPatrons(100 + (int)(Math.random() * 50));
            row.setLoans(40 + (int)(Math.random() * 20));
            row.setOverdues((int)(Math.random() * 5));
            rows.add(row);
        }
        response.setRows(rows);

        // Dummy popular items
        List<PopularItemDTO> popular = new ArrayList<>();
        PopularItemDTO item1 = new PopularItemDTO();
        item1.setTitle("The Great Gatsby");
        item1.setAuthor("F. Scott Fitzgerald");
        item1.setCheckouts(87);
        popular.add(item1);

        PopularItemDTO item2 = new PopularItemDTO();
        item2.setTitle("1984");
        item2.setAuthor("George Orwell");
        item2.setCheckouts(76);
        popular.add(item2);
        response.setPopularItems(popular);

        return response;
    }

    public SystemReportResponseDTO generateSystemReport(ReportRequestDTO request) {
        SystemReportResponseDTO response = new SystemReportResponseDTO();
        List<SystemReportRowDTO> rows = new ArrayList<>();

        LocalDate start = request.getFromDate() != null ? request.getFromDate() : LocalDate.now().minusDays(7);
        LocalDate end = request.getToDate() != null ? request.getToDate() : LocalDate.now();

        // Dummy data
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            SystemReportRowDTO row = new SystemReportRowDTO();
            row.setDate(date);
            row.setRobotId("Alpha, Beta");
            row.setEventsLogged("4 Err, 12 Warn");
            row.setAvgBattery(82.0 + Math.random() * 10);
            rows.add(row);
        }
        response.setRows(rows);
        return response;
    }
}


