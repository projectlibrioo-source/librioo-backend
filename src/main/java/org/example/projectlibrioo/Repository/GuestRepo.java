package org.example.projectlibrioo.Repository;

import org.example.projectlibrioo.Model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepo extends JpaRepository<Guest, Integer> {

    Guest findGuestByGuestID(int guestID);

}
