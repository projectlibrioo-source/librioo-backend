package org.example.projectlibrioo.Repository;

import org.example.projectlibrioo.Model.Fines;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FineRepo extends JpaRepository<Fines, Integer> {


    int findMaxLoanByCategory(String userCategory);

    double findFineByCategory(String userCategory);
}
