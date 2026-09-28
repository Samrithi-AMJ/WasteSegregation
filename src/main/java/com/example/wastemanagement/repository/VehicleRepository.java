package com.example.wastemanagement.repository;

import com.example.wastemanagement.entity.Vehicle;
import com.example.wastemanagement.entity.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByStatus(VehicleStatus status);

    boolean existsByVehicleNumber(String vehicleNumber);
}
