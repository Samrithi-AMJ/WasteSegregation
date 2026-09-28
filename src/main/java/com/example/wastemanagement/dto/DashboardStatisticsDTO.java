package com.example.wastemanagement.dto;

public class DashboardStatisticsDTO {
    private double totalWaste;
    private long totalCollections;
    private double wetWaste;
    private double dryWaste;
    private double plasticWaste;
    private double paperWaste;
    private double otherWaste;
    private double segregationRate;
    private long pendingCollections;
    private long missedCollections;
    private long collectedCollections;
    private long openComplaints;
    private long totalComplaints;

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

    public double getWetWaste() {
        return wetWaste;
    }

    public void setWetWaste(double wetWaste) {
        this.wetWaste = wetWaste;
    }

    public double getDryWaste() {
        return dryWaste;
    }

    public void setDryWaste(double dryWaste) {
        this.dryWaste = dryWaste;
    }

    public double getPlasticWaste() {
        return plasticWaste;
    }

    public void setPlasticWaste(double plasticWaste) {
        this.plasticWaste = plasticWaste;
    }

    public double getPaperWaste() {
        return paperWaste;
    }

    public void setPaperWaste(double paperWaste) {
        this.paperWaste = paperWaste;
    }

    public double getOtherWaste() {
        return otherWaste;
    }

    public void setOtherWaste(double otherWaste) {
        this.otherWaste = otherWaste;
    }

    public double getSegregationRate() {
        return segregationRate;
    }

    public void setSegregationRate(double segregationRate) {
        this.segregationRate = segregationRate;
    }

    public long getPendingCollections() {
        return pendingCollections;
    }

    public void setPendingCollections(long pendingCollections) {
        this.pendingCollections = pendingCollections;
    }

    public long getMissedCollections() {
        return missedCollections;
    }

    public void setMissedCollections(long missedCollections) {
        this.missedCollections = missedCollections;
    }

    public long getCollectedCollections() {
        return collectedCollections;
    }

    public void setCollectedCollections(long collectedCollections) {
        this.collectedCollections = collectedCollections;
    }

    public long getOpenComplaints() {
        return openComplaints;
    }

    public void setOpenComplaints(long openComplaints) {
        this.openComplaints = openComplaints;
    }

    public long getTotalComplaints() {
        return totalComplaints;
    }

    public void setTotalComplaints(long totalComplaints) {
        this.totalComplaints = totalComplaints;
    }
}
