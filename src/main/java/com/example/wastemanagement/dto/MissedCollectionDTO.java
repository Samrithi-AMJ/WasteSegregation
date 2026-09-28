package com.example.wastemanagement.dto;

import java.time.LocalDate;

public class MissedCollectionDTO {
    private Long collectionId;
    private String area;
    private String citizenName;
    private LocalDate lastCollectionDate;
    private long daysOverdue;
    private String status;

    public MissedCollectionDTO() {
    }

    public MissedCollectionDTO(Long collectionId, String area, String citizenName,
                                LocalDate lastCollectionDate, long daysOverdue, String status) {
        this.collectionId = collectionId;
        this.area = area;
        this.citizenName = citizenName;
        this.lastCollectionDate = lastCollectionDate;
        this.daysOverdue = daysOverdue;
        this.status = status;
    }

    public Long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCitizenName() {
        return citizenName;
    }

    public void setCitizenName(String citizenName) {
        this.citizenName = citizenName;
    }

    public LocalDate getLastCollectionDate() {
        return lastCollectionDate;
    }

    public void setLastCollectionDate(LocalDate lastCollectionDate) {
        this.lastCollectionDate = lastCollectionDate;
    }

    public long getDaysOverdue() {
        return daysOverdue;
    }

    public void setDaysOverdue(long daysOverdue) {
        this.daysOverdue = daysOverdue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
