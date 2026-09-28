package com.example.wastemanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "segregations")
public class Segregation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long segregationId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", nullable = false, unique = true)
    @NotNull(message = "Waste collection is required")
    private WasteCollection wasteCollection;

    @PositiveOrZero(message = "Wet quantity cannot be negative")
    @Column(name = "wet_quantity", nullable = false)
    private Double wetQuantity = 0.0;

    @PositiveOrZero(message = "Dry quantity cannot be negative")
    @Column(name = "dry_quantity", nullable = false)
    private Double dryQuantity = 0.0;

    @PositiveOrZero(message = "Plastic quantity cannot be negative")
    @Column(name = "plastic_quantity", nullable = false)
    private Double plasticQuantity = 0.0;

    @PositiveOrZero(message = "Paper quantity cannot be negative")
    @Column(name = "paper_quantity", nullable = false)
    private Double paperQuantity = 0.0;

    @PositiveOrZero(message = "Other quantity cannot be negative")
    @Column(name = "other_quantity", nullable = false)
    private Double otherQuantity = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "segregation_status", nullable = false)
    private SegregationStatus segregationStatus;

    public Segregation() {
    }

    /** Total quantity across all waste categories, computed on demand. */
    @Transient
    public Double getTotalQuantity() {
        double wet = wetQuantity == null ? 0 : wetQuantity;
        double dry = dryQuantity == null ? 0 : dryQuantity;
        double plastic = plasticQuantity == null ? 0 : plasticQuantity;
        double paper = paperQuantity == null ? 0 : paperQuantity;
        double other = otherQuantity == null ? 0 : otherQuantity;
        return wet + dry + plastic + paper + other;
    }

    public Long getSegregationId() {
        return segregationId;
    }

    public void setSegregationId(Long segregationId) {
        this.segregationId = segregationId;
    }

    public WasteCollection getWasteCollection() {
        return wasteCollection;
    }

    public void setWasteCollection(WasteCollection wasteCollection) {
        this.wasteCollection = wasteCollection;
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
}
