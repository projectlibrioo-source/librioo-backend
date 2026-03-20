package org.example.projectlibrioo.Repository;

import org.example.projectlibrioo.Model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepo extends JpaRepository<Admin, Integer> {
    Admin findByAdminUsername(String adminUsername);
}
