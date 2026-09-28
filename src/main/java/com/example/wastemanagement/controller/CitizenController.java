package com.example.wastemanagement.controller;

import com.example.wastemanagement.dto.CitizenDTO;
import com.example.wastemanagement.service.CitizenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citizens")
public class CitizenController {

    @Autowired
    private CitizenService citizenService;

    @GetMapping
    public ResponseEntity<List<CitizenDTO>> getAllCitizens(
            @RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(citizenService.searchByName(search));
        }
        return ResponseEntity.ok(citizenService.getAllCitizens());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitizenDTO> getCitizenById(@PathVariable Long id) {
        return ResponseEntity.ok(citizenService.getCitizenById(id));
    }

    @PostMapping
    public ResponseEntity<CitizenDTO> createCitizen(@Valid @RequestBody CitizenDTO dto) {
        return new ResponseEntity<>(citizenService.createCitizen(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CitizenDTO> updateCitizen(@PathVariable Long id, @Valid @RequestBody CitizenDTO dto) {
        return ResponseEntity.ok(citizenService.updateCitizen(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCitizen(@PathVariable Long id) {
        citizenService.deleteCitizen(id);
        return ResponseEntity.noContent().build();
    }
}
