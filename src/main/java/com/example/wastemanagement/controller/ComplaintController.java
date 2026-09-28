package com.example.wastemanagement.controller;

import com.example.wastemanagement.dto.ComplaintDTO;
import com.example.wastemanagement.entity.ComplaintStatus;
import com.example.wastemanagement.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @GetMapping
    public ResponseEntity<List<ComplaintDTO>> getAllComplaints(
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(required = false) Long citizenId) {
        if (status != null) {
            return ResponseEntity.ok(complaintService.getComplaintsByStatus(status));
        }
        if (citizenId != null) {
            return ResponseEntity.ok(complaintService.getComplaintsByCitizen(citizenId));
        }
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintDTO> getComplaintById(@PathVariable Long id) {
        return ResponseEntity.ok(complaintService.getComplaintById(id));
    }

    @PostMapping
    public ResponseEntity<ComplaintDTO> createComplaint(@Valid @RequestBody ComplaintDTO dto) {
        return new ResponseEntity<>(complaintService.createComplaint(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComplaintDTO> updateComplaint(@PathVariable Long id,
                                                          @Valid @RequestBody ComplaintDTO dto) {
        return ResponseEntity.ok(complaintService.updateComplaint(id, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ComplaintDTO> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        ComplaintStatus status = ComplaintStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(complaintService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long id) {
        complaintService.deleteComplaint(id);
        return ResponseEntity.noContent().build();
    }
}
