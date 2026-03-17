package org.example.projectlibrioo.Service.Admin;

import org.example.projectlibrioo.Model.DashboardDTO;
import org.example.projectlibrioo.Repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
@Service
public class DashboardService {

    private final BookRepo bookRepository;
    private final MemberRepo memberRepository;
    private final GuestRepo guestRepository;
    private final RobotRepo robotRepository;
    private final TransactionRepo transactionsRepository;

    public DashboardService(BookRepo bookRepository,
                            MemberRepo memberRepository,
                            GuestRepo guestRepository,
                            RobotRepo robotRepository,
                            TransactionRepo transactionsRepository) {

        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.guestRepository = guestRepository;
        this.robotRepository = robotRepository;
        this.transactionsRepository = transactionsRepository;
    }

    public DashboardDTO getDashboardData() {

        long totalBooks = bookRepository.count();

        long totalMembers = memberRepository.count();
        long totalGuests = guestRepository.count();
        long totalUsers = totalMembers + totalGuests;

        long activeUsers = memberRepository.countByStatus("ACTIVE");

        long booksBorrowed = transactionsRepository.countByStatus("BORROWED");

        long overdueBooks = transactionsRepository
                .countByReturnDateBeforeAndStatus(LocalDate.now(), "BORROWED");

        long activeRobots = robotRepository.countByStatus("ACTIVE");

        return new DashboardDTO(
                totalBooks,
                totalUsers,
                activeUsers,
                booksBorrowed,
                overdueBooks,
                activeRobots
        );
    }
}
