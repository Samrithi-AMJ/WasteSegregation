package com.example.wastemanagement.controller;

import com.example.wastemanagement.dto.AreaStatisticsDTO;
import com.example.wastemanagement.dto.DashboardStatisticsDTO;
import com.example.wastemanagement.dto.MissedCollectionDTO;
import com.example.wastemanagement.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/statistics")
    public ResponseEntity<DashboardStatisticsDTO> getStatistics() {
        return ResponseEntity.ok(dashboardService.getStatistics());
    }

    @GetMapping("/waste-by-type")
    public ResponseEntity<Map<String, Double>> getWasteByType() {
        return ResponseEntity.ok(dashboardService.getWasteByType());
    }

    @GetMapping("/waste-by-area")
    public ResponseEntity<List<AreaStatisticsDTO>> getWasteByArea() {
        return ResponseEntity.ok(dashboardService.getWasteByArea());
    }

    @GetMapping("/collection-status")
    public ResponseEntity<Map<String, Long>> getCollectionStatus() {
        return ResponseEntity.ok(dashboardService.getCollectionStatusBreakdown());
    }

    @GetMapping("/complaint-status")
    public ResponseEntity<Map<String, Long>> getComplaintStatus() {
        return ResponseEntity.ok(dashboardService.getComplaintStatusBreakdown());
    }

    @GetMapping("/missed-collections")
    public ResponseEntity<List<MissedCollectionDTO>> getMissedCollections() {
        return ResponseEntity.ok(dashboardService.getMissedCollections());
    }
}
