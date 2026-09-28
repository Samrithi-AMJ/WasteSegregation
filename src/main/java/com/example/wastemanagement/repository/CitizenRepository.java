package com.example.wastemanagement.repository;

import com.example.wastemanagement.entity.Citizen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitizenRepository extends JpaRepository<Citizen, Long> {

    List<Citizen> findByAreaIgnoreCase(String area);

    List<Citizen> findByNameContainingIgnoreCase(String name);

    boolean existsByEmail(String email);
}
