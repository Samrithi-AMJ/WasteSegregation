package com.example.wastemanagement.repository;

import com.example.wastemanagement.entity.Worker;
import com.example.wastemanagement.entity.WorkerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkerRepository extends JpaRepository<Worker, Long> {

    List<Worker> findByAssignedAreaIgnoreCase(String area);

    List<Worker> findByStatus(WorkerStatus status);
}
