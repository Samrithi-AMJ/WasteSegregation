package com.example.wastemanagement;

import com.example.wastemanagement.dto.ComplaintDTO;
import com.example.wastemanagement.entity.Citizen;
import com.example.wastemanagement.entity.Complaint;
import com.example.wastemanagement.entity.ComplaintStatus;
import com.example.wastemanagement.exception.ResourceNotFoundException;
import com.example.wastemanagement.repository.CitizenRepository;
import com.example.wastemanagement.repository.ComplaintRepository;
import com.example.wastemanagement.service.ComplaintService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ComplaintServiceTest {

    @Mock
    private ComplaintRepository complaintRepository;

    @Mock
    private CitizenRepository citizenRepository;

    @InjectMocks
    private ComplaintService complaintService;

    private Citizen citizen;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        citizen = new Citizen();
        citizen.setCitizenId(1L);
        citizen.setName("Priya Raman");
    }

    @Test
    void createComplaint_setsStatusPendingByDefault() {
        when(citizenRepository.findById(1L)).thenReturn(Optional.of(citizen));
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(inv -> {
            Complaint c = inv.getArgument(0);
            c.setComplaintId(10L);
            return c;
        });

        ComplaintDTO dto = new ComplaintDTO();
        dto.setCitizenId(1L);
        dto.setLocation("Near Bus Stand");
        dto.setDescription("Garbage not collected for two days.");

        ComplaintDTO result = complaintService.createComplaint(dto);

        assertEquals(ComplaintStatus.PENDING, result.getStatus());
        assertEquals("Near Bus Stand", result.getLocation());
    }

    @Test
    void updateStatus_changesComplaintStatus() {
        Complaint complaint = new Complaint();
        complaint.setComplaintId(5L);
        complaint.setCitizen(citizen);
        complaint.setLocation("Market Street");
        complaint.setDescription("Overflowing bin");
        complaint.setComplaintDate(LocalDate.now());
        complaint.setStatus(ComplaintStatus.PENDING);

        when(complaintRepository.findById(5L)).thenReturn(Optional.of(complaint));
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(inv -> inv.getArgument(0));

        ComplaintDTO result = complaintService.updateStatus(5L, ComplaintStatus.RESOLVED);

        assertEquals(ComplaintStatus.RESOLVED, result.getStatus());
    }

    @Test
    void getComplaintById_throwsResourceNotFound() {
        when(complaintRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> complaintService.getComplaintById(100L));
    }
}
