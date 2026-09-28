package com.example.wastemanagement;

import com.example.wastemanagement.dto.DashboardStatisticsDTO;
import com.example.wastemanagement.entity.CollectionStatus;
import com.example.wastemanagement.entity.WasteType;
import com.example.wastemanagement.repository.ComplaintRepository;
import com.example.wastemanagement.repository.SegregationRepository;
import com.example.wastemanagement.repository.WasteCollectionRepository;
import com.example.wastemanagement.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class DashboardServiceTest {

    @Mock
    private WasteCollectionRepository collectionRepository;

    @Mock
    private SegregationRepository segregationRepository;

    @Mock
    private ComplaintRepository complaintRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getStatistics_calculatesTotalWasteFromAllTypes() {
        when(collectionRepository.sumWasteByType(WasteType.WET)).thenReturn(500.0);
        when(collectionRepository.sumWasteByType(WasteType.DRY)).thenReturn(300.0);
        when(collectionRepository.sumWasteByType(WasteType.PLASTIC)).thenReturn(100.0);
        when(collectionRepository.sumWasteByType(WasteType.PAPER)).thenReturn(50.0);
        when(collectionRepository.sumWasteByType(WasteType.OTHER)).thenReturn(50.0);
        when(collectionRepository.sumTotalWaste()).thenReturn(1000.0);
        when(collectionRepository.count()).thenReturn(20L);
        when(collectionRepository.countByStatus(CollectionStatus.COLLECTED)).thenReturn(15L);
        when(collectionRepository.countByStatus(CollectionStatus.PENDING)).thenReturn(3L);
        when(collectionRepository.countByStatus(CollectionStatus.MISSED)).thenReturn(2L);
        when(segregationRepository.findAll()).thenReturn(Collections.emptyList());
        when(complaintRepository.count()).thenReturn(10L);

        DashboardStatisticsDTO stats = dashboardService.getStatistics();

        assertEquals(1000.0, stats.getTotalWaste());
        assertEquals(20L, stats.getTotalCollections());
        assertEquals(2L, stats.getMissedCollections());
    }
}
