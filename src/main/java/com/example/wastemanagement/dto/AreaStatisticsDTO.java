package com.example.wastemanagement.dto;

public class AreaStatisticsDTO {
    private String area;
    private double totalWaste;
    private long totalCollections;
    private long missedCollections;
    private double segregationRate;

    public AreaStatisticsDTO() {
    }

    public AreaStatisticsDTO(String area, double totalWaste, long totalCollections,
                              long missedCollections, double segregationRate) {
        this.area = area;
        this.totalWaste = totalWaste;
        this.totalCollections = totalCollections;
        this.missedCollections = missedCollections;
        this.segregationRate = segregationRate;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public double getTotalWaste() {
        return totalWaste;
    }

    public void setTotalWaste(double totalWaste) {
        this.totalWaste = totalWaste;
    }

    public long getTotalCollections() {
        return totalCollections;
    }

    public void setTotalCollections(long totalCollections) {
        this.totalCollections = totalCollections;
    }

    public long getMissedCollections() {
        return missedCollections;
    }

    public void setMissedCollections(long missedCollections) {
        this.missedCollections = missedCollections;
    }

    public double getSegregationRate() {
        return segregationRate;
    }

    public void setSegregationRate(double segregationRate) {
        this.segregationRate = segregationRate;
    }
}
