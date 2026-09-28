package com.example.wastemanagement.service;

import com.example.wastemanagement.dto.AreaStatisticsDTO;
import com.example.wastemanagement.dto.DashboardStatisticsDTO;
import com.example.wastemanagement.dto.MissedCollectionDTO;
import com.example.wastemanagement.entity.*;
import com.example.wastemanagement.repository.ComplaintRepository;
import com.example.wastemanagement.repository.SegregationRepository;
import com.example.wastemanagement.repository.WasteCollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Holds the "smart" logic of the application: aggregate waste statistics,
 * segregation percentage, missed/overdue collection detection and
 * area-wise reporting. All numbers are computed live from the database -
 * nothing here is hard-coded.
 */
@Service
public class DashboardService {

    /** Waste is expected to be collected from every household at least once every N days. */
    private static final int COLLECTION_INTERVAL_DAYS = 7;

    @Autowired
    private WasteCollectionRepository collectionRepository;

    @Autowired
    private SegregationRepository segregationRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    // ---------------------------------------------------------------
    // Feature 1 & 2 & 3 & 4: overall dashboard statistics
    // ---------------------------------------------------------------
    public DashboardStatisticsDTO getStatistics() {
        DashboardStatisticsDTO dto = new DashboardStatisticsDTO();

        double wet = safe(collectionRepository.sumWasteByType(WasteType.WET));
        double dry = safe(collectionRepository.sumWasteByType(WasteType.DRY));
        double plastic = safe(collectionRepository.sumWasteByType(WasteType.PLASTIC));
        double paper = safe(collectionRepository.sumWasteByType(WasteType.PAPER));
        double other = safe(collectionRepository.sumWasteByType(WasteType.OTHER));
        double totalWaste = wet + dry + plastic + paper + other;

        dto.setWetWaste(wet);
        dto.setDryWaste(dry);
        dto.setPlasticWaste(plastic);
        dto.setPaperWaste(paper);
        dto.setOtherWaste(other);
        dto.setTotalWaste(totalWaste);

        dto.setTotalCollections(collectionRepository.count());
        dto.setCollectedCollections(collectionRepository.countByStatus(CollectionStatus.COLLECTED));
        dto.setPendingCollections(collectionRepository.countByStatus(CollectionStatus.PENDING));
        dto.setMissedCollections(collectionRepository.countByStatus(CollectionStatus.MISSED));

        dto.setSegregationRate(calculateSegregationRate());

        dto.setTotalComplaints(complaintRepository.count());
        dto.setOpenComplaints(complaintRepository.countByStatusNot(ComplaintStatus.RESOLVED)
                - complaintRepository.countByStatus(ComplaintStatus.REJECTED));

        return dto;
    }

    // ---------------------------------------------------------------
    // Feature 2: waste by type (for the pie/bar chart)
    // ---------------------------------------------------------------
    public Map<String, Double> getWasteByType() {
        Map<String, Double> map = new LinkedHashMap<>();
        for (WasteType type : WasteType.values()) {
            map.put(type.name(), safe(collectionRepository.sumWasteByType(type)));
        }
        return map;
    }

    // ---------------------------------------------------------------
    // Feature 6: waste by area
    // ---------------------------------------------------------------
    public List<AreaStatisticsDTO> getWasteByArea() {
        List<String> areas = collectionRepository.findDistinctAreas();
        List<AreaStatisticsDTO> result = new ArrayList<>();
        for (String area : areas) {
            List<WasteCollection> areaCollections = collectionRepository.findByAreaIgnoreCase(area);
            double totalWaste = areaCollections.stream().mapToDouble(WasteCollection::getQuantity).sum();
            long totalCollections = areaCollections.size();
            long missed = areaCollections.stream()
                    .filter(c -> c.getStatus() == CollectionStatus.MISSED).count();
            double segRate = calculateSegregationRateForCollections(areaCollections);
            result.add(new AreaStatisticsDTO(area, totalWaste, totalCollections, missed, segRate));
        }
        result.sort(Comparator.comparing(AreaStatisticsDTO::getArea));
        return result;
    }

    // ---------------------------------------------------------------
    // Feature 3: collection status breakdown
    // ---------------------------------------------------------------
    public Map<String, Long> getCollectionStatusBreakdown() {
        Map<String, Long> map = new LinkedHashMap<>();
        for (CollectionStatus status : CollectionStatus.values()) {
            map.put(status.name(), collectionRepository.countByStatus(status));
        }
        return map;
    }

    // ---------------------------------------------------------------
    // Feature 7: complaint status breakdown
    // ---------------------------------------------------------------
    public Map<String, Long> getComplaintStatusBreakdown() {
        Map<String, Long> map = new LinkedHashMap<>();
        for (ComplaintStatus status : ComplaintStatus.values()) {
            map.put(status.name(), complaintRepository.countByStatus(status));
        }
        return map;
    }

    // ---------------------------------------------------------------
    // Feature 5: missed / overdue collection detection
    //
    // A collection record is considered overdue when:
    //   1) it is already explicitly marked MISSED, or
    //   2) it is still PENDING and its scheduled collectionDate is
    //      older than COLLECTION_INTERVAL_DAYS from today.
    // ---------------------------------------------------------------
    public List<MissedCollectionDTO> getMissedCollections() {
        LocalDate today = LocalDate.now();
        List<WasteCollection> all = collectionRepository.findAll();

        return all.stream()
                .filter(c -> c.getStatus() == CollectionStatus.MISSED
                        || (c.getStatus() == CollectionStatus.PENDING
                            && ChronoUnit.DAYS.between(c.getCollectionDate(), today) > COLLECTION_INTERVAL_DAYS))
                .map(c -> new MissedCollectionDTO(
                        c.getCollectionId(),
                        c.getArea(),
                        c.getCitizen().getName(),
                        c.getCollectionDate(),
                        Math.max(0, ChronoUnit.DAYS.between(c.getCollectionDate(), today)),
                        c.getStatus().name()
                ))
                .sorted(Comparator.comparingLong(MissedCollectionDTO::getDaysOverdue).reversed())
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------------
    // Feature 4: segregation percentage helpers
    // ---------------------------------------------------------------
    private double calculateSegregationRate() {
        double totalWaste = safe(collectionRepository.sumTotalWaste());
        if (totalWaste <= 0) {
            return 0.0;
        }
        double properlySegregated = segregationRepository.findAll().stream()
                .filter(s -> s.getSegregationStatus() == SegregationStatus.PROPER)
                .mapToDouble(Segregation::getTotalQuantity)
                .sum();
        return round(properlySegregated / totalWaste * 100);
    }

    private double calculateSegregationRateForCollections(List<WasteCollection> collections) {
        double totalWaste = collections.stream().mapToDouble(WasteCollection::getQuantity).sum();
        if (totalWaste <= 0) {
            return 0.0;
        }
        double properlySegregated = collections.stream()
                .map(WasteCollection::getSegregation)
                .filter(Objects::nonNull)
                .filter(s -> s.getSegregationStatus() == SegregationStatus.PROPER)
                .mapToDouble(Segregation::getTotalQuantity)
                .sum();
        return round(properlySegregated / totalWaste * 100);
    }

    private double safe(Double value) {
        return value == null ? 0.0 : value;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
