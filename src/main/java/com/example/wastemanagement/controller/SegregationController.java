package com.example.wastemanagement.controller;

import com.example.wastemanagement.dto.SegregationDTO;
import com.example.wastemanagement.service.SegregationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/segregations")
public class SegregationController {

    @Autowired
    private SegregationService segregationService;

    @GetMapping
    public ResponseEntity<List<SegregationDTO>> getAllSegregations() {
        return ResponseEntity.ok(segregationService.getAllSegregations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SegregationDTO> getSegregationById(@PathVariable Long id) {
        return ResponseEntity.ok(segregationService.getSegregationById(id));
    }

    @PostMapping
    public ResponseEntity<SegregationDTO> createSegregation(@Valid @RequestBody SegregationDTO dto) {
        return new ResponseEntity<>(segregationService.createSegregation(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SegregationDTO> updateSegregation(@PathVariable Long id,
                                                              @Valid @RequestBody SegregationDTO dto) {
        return ResponseEntity.ok(segregationService.updateSegregation(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSegregation(@PathVariable Long id) {
        segregationService.deleteSegregation(id);
        return ResponseEntity.noContent().build();
    }
}
