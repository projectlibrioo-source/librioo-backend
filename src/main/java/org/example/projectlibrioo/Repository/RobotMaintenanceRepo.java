package org.example.projectlibrioo.Repository;

import org.example.projectlibrioo.Model.RobotMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RobotMaintenanceRepo extends JpaRepository<RobotMaintenance, Integer> {

    // Get all maintenance logs for a specific robot, newest first
    List<RobotMaintenance> findByRobot_RobotIDOrderByLoggedAtDesc(int robotId);
}
