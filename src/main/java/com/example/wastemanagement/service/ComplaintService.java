package com.example.wastemanagement.service;

import com.example.wastemanagement.dto.ComplaintDTO;
import com.example.wastemanagement.entity.Citizen;
import com.example.wastemanagement.entity.Complaint;
import com.example.wastemanagement.entity.ComplaintStatus;
import com.example.wastemanagement.exception.ResourceNotFoundException;
import com.example.wastemanagement.repository.CitizenRepository;
import com.example.wastemanagement.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private CitizenRepository citizenRepository;

    public List<ComplaintDTO> getAllComplaints() {
        return complaintRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ComplaintDTO getComplaintById(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", id));
        return toDTO(complaint);
    }

    public List<ComplaintDTO> getComplaintsByCitizen(Long citizenId) {
        return complaintRepository.findByCitizen_CitizenId(citizenId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ComplaintDTO> getComplaintsByStatus(ComplaintStatus status) {
        return complaintRepository.findByStatus(status)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ComplaintDTO createComplaint(ComplaintDTO dto) {
        Citizen citizen = citizenRepository.findById(dto.getCitizenId())
                .orElseThrow(() -> new ResourceNotFoundException("Citizen", dto.getCitizenId()));

        Complaint complaint = new Complaint();
        complaint.setCitizen(citizen);
        complaint.setLocation(dto.getLocation());
        complaint.setDescription(dto.getDescription());
        complaint.setComplaintDate(dto.getComplaintDate() != null ? dto.getComplaintDate() : LocalDate.now());
        complaint.setStatus(ComplaintStatus.PENDING);
        return toDTO(complaintRepository.save(complaint));
    }

    public ComplaintDTO updateComplaint(Long id, ComplaintDTO dto) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", id));
        complaint.setLocation(dto.getLocation());
        complaint.setDescription(dto.getDescription());
        if (dto.getStatus() != null) {
            complaint.setStatus(dto.getStatus());
        }
        return toDTO(complaintRepository.save(complaint));
    }

    public ComplaintDTO updateStatus(Long id, ComplaintStatus status) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", id));
        complaint.setStatus(status);
        return toDTO(complaintRepository.save(complaint));
    }

    public void deleteComplaint(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", id));
        complaintRepository.delete(complaint);
    }

    private ComplaintDTO toDTO(Complaint complaint) {
        ComplaintDTO dto = new ComplaintDTO();
        dto.setComplaintId(complaint.getComplaintId());
        dto.setCitizenId(complaint.getCitizen().getCitizenId());
        dto.setCitizenName(complaint.getCitizen().getName());
        dto.setLocation(complaint.getLocation());
        dto.setDescription(complaint.getDescription());
        dto.setComplaintDate(complaint.getComplaintDate());
        dto.setStatus(complaint.getStatus());
        return dto;
    }
}
