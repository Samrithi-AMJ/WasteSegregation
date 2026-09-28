package com.example.wastemanagement.dto;

import com.example.wastemanagement.entity.CollectionStatus;
import com.example.wastemanagement.entity.WasteType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

/**
 * Flat DTO used for both requests (create/update) and responses.
 * Using ids for relationships (citizenId, workerId, vehicleId) avoids
 * circular JSON serialization problems with the entity graph.
 */
public class WasteCollectionDTO {

    private Long collectionId;

    @NotNull(message = "Citizen is required")
    private Long citizenId;

    private String citizenName;

    private Long workerId;

    private String workerName;

    private Long vehicleId;

    private String vehicleNumber;

    @NotNull(message = "Collection date is required")
    private LocalDate collectionDate;

    @NotBlank(message = "Area is required")
    private String area;

    @NotNull(message = "Waste type is required")
    private WasteType wasteType;

    @Positive(message = "Quantity must be greater than zero")
    private Double quantity;

    private CollectionStatus status;

    private Boolean segregated;

    public Long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }

    public Long getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(Long citizenId) {
        this.citizenId = citizenId;
    }

    public String getCitizenName() {
        return citizenName;
    }

    public void setCitizenName(String citizenName) {
        this.citizenName = citizenName;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public LocalDate getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(LocalDate collectionDate) {
        this.collectionDate = collectionDate;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public WasteType getWasteType() {
        return wasteType;
    }

    public void setWasteType(WasteType wasteType) {
        this.wasteType = wasteType;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public CollectionStatus getStatus() {
        return status;
    }

    public void setStatus(CollectionStatus status) {
        this.status = status;
    }

    public Boolean getSegregated() {
        return segregated;
    }

    public void setSegregated(Boolean segregated) {
        this.segregated = segregated;
    }
}
