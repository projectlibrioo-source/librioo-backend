package org.example.projectlibrioo.Repository;

import org.example.projectlibrioo.Model.Robot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RobotRepo extends JpaRepository<Robot, Integer> {
    Optional<Robot> findByRobotName(String robotName);
    List<Robot> findByStatus(String status);
    boolean existsByRobotName(String robotName);
}
