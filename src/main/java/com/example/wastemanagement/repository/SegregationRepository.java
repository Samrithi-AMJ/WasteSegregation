package com.example.wastemanagement.repository;

import com.example.wastemanagement.entity.Segregation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SegregationRepository extends JpaRepository<Segregation, Long> {

    Optional<Segregation> findByWasteCollection_CollectionId(Long collectionId);

    boolean existsByWasteCollection_CollectionId(Long collectionId);
}
