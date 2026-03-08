package org.example.projectlibrioo.Repository;

import org.example.projectlibrioo.Model.Robot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RobotRepo extends JpaRepository<Robot, Integer> {
}
