package com.example.wastemanagement.repository;

import com.example.wastemanagement.entity.CollectionStatus;
import com.example.wastemanagement.entity.WasteCollection;
import com.example.wastemanagement.entity.WasteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface WasteCollectionRepository extends JpaRepository<WasteCollection, Long> {

    List<WasteCollection> findByAreaIgnoreCase(String area);

    List<WasteCollection> findByStatus(CollectionStatus status);

    List<WasteCollection> findByWasteType(WasteType wasteType);

    List<WasteCollection> findByCollectionDateBetween(LocalDate start, LocalDate end);

    List<WasteCollection> findByCitizen_CitizenId(Long citizenId);

    List<WasteCollection> findByWorker_WorkerId(Long workerId);

    List<WasteCollection> findByVehicle_VehicleId(Long vehicleId);

    @Query("SELECT COALESCE(SUM(w.quantity), 0) FROM WasteCollection w")
    Double sumTotalWaste();

    @Query("SELECT COALESCE(SUM(w.quantity), 0) FROM WasteCollection w WHERE w.wasteType = :type")
    Double sumWasteByType(@Param("type") WasteType type);

    @Query("SELECT COALESCE(SUM(w.quantity), 0) FROM WasteCollection w WHERE w.area = :area")
    Double sumWasteByArea(@Param("area") String area);

    @Query("SELECT DISTINCT w.area FROM WasteCollection w")
    List<String> findDistinctAreas();

    long countByStatus(CollectionStatus status);

    long countByArea(String area);
}
