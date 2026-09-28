package com.example.wastemanagement.repository;

import com.example.wastemanagement.entity.Complaint;
import com.example.wastemanagement.entity.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByStatus(ComplaintStatus status);

    List<Complaint> findByCitizen_CitizenId(Long citizenId);

    long countByStatus(ComplaintStatus status);

    long countByStatusNot(ComplaintStatus status);
}
