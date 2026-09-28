package com.example.wastemanagement.dto;

import com.example.wastemanagement.entity.SegregationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class SegregationDTO {

    private Long segregationId;

    @NotNull(message = "Collection id is required")
    private Long collectionId;

    @PositiveOrZero(message = "Wet quantity cannot be negative")
    private Double wetQuantity = 0.0;

    @PositiveOrZero(message = "Dry quantity cannot be negative")
    private Double dryQuantity = 0.0;

    @PositiveOrZero(message = "Plastic quantity cannot be negative")
    private Double plasticQuantity = 0.0;

    @PositiveOrZero(message = "Paper quantity cannot be negative")
    private Double paperQuantity = 0.0;

    @PositiveOrZero(message = "Other quantity cannot be negative")
    private Double otherQuantity = 0.0;

    private SegregationStatus segregationStatus;

    private Double totalQuantity;

    public Long getSegregationId() {
        return segregationId;
    }

    public void setSegregationId(Long segregationId) {
        this.segregationId = segregationId;
    }

    public Long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }

    public Double getWetQuantity() {
        return wetQuantity;
    }

    public void setWetQuantity(Double wetQuantity) {
        this.wetQuantity = wetQuantity;
    }

    public Double getDryQuantity() {
        return dryQuantity;
    }

    public void setDryQuantity(Double dryQuantity) {
        this.dryQuantity = dryQuantity;
    }

    public Double getPlasticQuantity() {
        return plasticQuantity;
    }

    public void setPlasticQuantity(Double plasticQuantity) {
        this.plasticQuantity = plasticQuantity;
    }

    public Double getPaperQuantity() {
        return paperQuantity;
    }

    public void setPaperQuantity(Double paperQuantity) {
        this.paperQuantity = paperQuantity;
    }

    public Double getOtherQuantity() {
        return otherQuantity;
    }

    public void setOtherQuantity(Double otherQuantity) {
        this.otherQuantity = otherQuantity;
    }

    public SegregationStatus getSegregationStatus() {
        return segregationStatus;
    }

    public void setSegregationStatus(SegregationStatus segregationStatus) {
        this.segregationStatus = segregationStatus;
    }

    public Double getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Double totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
}
